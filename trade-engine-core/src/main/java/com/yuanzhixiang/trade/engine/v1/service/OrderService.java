package com.yuanzhixiang.trade.engine.v1.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.v1.converter.OrderWebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.OrderBO;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;
import com.yuanzhixiang.trade.engine.v1.model.po.OrderPO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.CanCancelOrderVO;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerWhile;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit.OptimisticLockerKit;

import com.yuanzhixiang.trade.engine.v1.dao.HoldPositionMapper;
import com.yuanzhixiang.trade.engine.v1.dao.OrderMapper;
import com.yuanzhixiang.trade.engine.v1.dao.WalletMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/14 20:44:52
 */
@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderWebConverter orderWebConverter;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private HoldPositionMapper holdPositionMapper;

    @OptimisticLockerWhile
    @Transactional(rollbackFor = RuntimeException.class)
    public void insertCreateOrder(OrderBO orderBO) {
        // 买卖单需要分开处理
        if (OrderSideEnum.isBuyOrder(orderBO.getSide())) {
            doCreateBuyOrder(orderBO);
        } else {
            doCreateSellOrder(orderBO);
        }
    }

    private void doCreateSellOrder(OrderBO orderBO) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            OrderPO orderPO = orderWebConverter.boToPo(orderBO);

            // 判断是否有足够的数量可以卖出
            LambdaQueryWrapper<HoldPositionPO> holdPositionWrapper = new LambdaQueryWrapper<>();
            holdPositionWrapper.eq(HoldPositionPO::getUserId, orderPO.getUserId());
            holdPositionWrapper.eq(HoldPositionPO::getSymbol, orderPO.getSymbol());
            HoldPositionPO holdPositionPO = holdPositionMapper.selectOne(holdPositionWrapper);

            if (holdPositionPO == null) {
                throw new RuntimeException(StrUtil.format(
                    "用户 [{}] 没有持仓无法卖出", orderBO.getUserId()));
            }

            if (holdPositionPO.getPositionAmount() < orderPO.getAmount()) {
                throw new RuntimeException(StrUtil.format("用户 [{}] 持仓数量为 [{}]，期望卖出 [{}] 失败",
                    orderPO.getUserId(), holdPositionPO.getPositionAmount(), orderBO.getAmount()));
            }

            orderMapper.insert(orderPO);
            // 回写 id
            orderBO.setId(orderPO.getId());

            holdPositionPO.setPositionAmount(holdPositionPO.getPositionAmount() - orderPO.getAmount());
            holdPositionPO
                .setFrozenSellAmount(holdPositionPO.getFrozenSellAmount() + orderPO.getAmount());

            return holdPositionMapper.updateById(holdPositionPO) == 1;
        });
    }

    private void doCreateBuyOrder(OrderBO orderBO) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            OrderPO orderPO = orderWebConverter.boToPo(orderBO);

            // 买单需要判断钱包的钱是否够下单
            LambdaQueryWrapper<WalletPO> walletWrapper = new LambdaQueryWrapper<>();
            walletWrapper.eq(WalletPO::getUserId, orderPO.getUserId());
            WalletPO walletPO = walletMapper.selectOne(walletWrapper);
            if (walletPO == null) {
                throw new RuntimeException("该用户的钱包未初始化");
            }

            double fund = orderPO.getAmount() * orderPO.getPrice();
            if (walletPO.getBalance() < fund) {
                throw new RuntimeException("余额不够下单");
            }

            orderMapper.insert(orderPO);

            // 回写 id
            orderBO.setId(orderPO.getId());

            // 冻结用于下单的资金
            walletPO.setBalance(walletPO.getBalance() - fund);
            walletPO.setFrozenFund(walletPO.getFrozenFund() + fund);

            return walletMapper.updateById(walletPO) == 1;
        });

    }

    public void insertCancelOrder(OrderBO orderBO) {
        OrderPO orderPO = orderWebConverter.boToPo(orderBO);
        orderMapper.insert(orderPO);
        orderBO.setId(orderPO.getId());
    }

    /**
     * 查询用户当前能够撤销的订单
     *
     * @param userId 用户 id
     * @return 可撤单
     */
    public List<CanCancelOrderVO> selectCanCancelOrder(Long userId) {
        LambdaQueryWrapper<OrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OrderPO::getUserId, userId);
        List<OrderPO> orderPOList = orderMapper.selectList(orderWrapper);

        if (CollectionUtil.isEmpty(orderPOList)) {
            return Collections.emptyList();
        }

        return orderPOList.stream()
            // 过滤掉撤单
            .filter(orderPO -> OrderActionEnum.isCreate(orderPO.getAction()))
            // 过滤掉已经完成成交的委托单
            .filter(orderPO -> orderPO.getAmount() !=
                orderPO.getTradeAmount() + orderPO.getCancelAmount())
            // 转换类型
            .map(orderPO -> {
                CanCancelOrderVO canCancelOrderVO = new CanCancelOrderVO();
                canCancelOrderVO.setOrderId(orderPO.getId());
                canCancelOrderVO.setSymbol(orderPO.getSymbol());
                canCancelOrderVO.setAmount(orderPO.getAmount());
                canCancelOrderVO.setPrice(orderPO.getPrice());
                canCancelOrderVO.setTradeAmount(orderPO.getTradeAmount());
                canCancelOrderVO.setSide(orderPO.getSide());
                canCancelOrderVO.setCreateTime(orderPO.getGmtCreate());
                return canCancelOrderVO;
            })
            .collect(Collectors.toList());
    }

    /**
     * 按 id 查询委托单，带行锁
     *
     * @param orderId 委托单 id
     * @return 委托单信息
     */
    public OrderPO selectOrderById(Long orderId) {
        LambdaQueryWrapper<OrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OrderPO::getId, orderId);

        return orderMapper.selectOne(orderWrapper);
    }

    public List<OrderBO> selectOrderListByUserId(Long userId) {
        LambdaQueryWrapper<OrderPO> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(OrderPO::getUserId, userId);
        orderWrapper.eq(OrderPO::getAction, OrderActionEnum.CREATE.name());
        return orderWebConverter.poToBo(orderMapper.selectList(orderWrapper));
    }
}
