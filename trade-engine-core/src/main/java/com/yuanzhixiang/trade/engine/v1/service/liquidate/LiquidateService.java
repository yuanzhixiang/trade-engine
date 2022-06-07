package com.yuanzhixiang.trade.engine.v1.service.liquidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lmax.disruptor.EventHandler;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerWhile;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit.OptimisticLockerKit;
import com.yuanzhixiang.trade.engine.trade.web.log.annotation.CatchException;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.VoucherEvent;
import com.yuanzhixiang.trade.engine.v1.converter.TradeVoucherConverter;
import com.yuanzhixiang.trade.engine.v1.dao.HoldPositionMapper;
import com.yuanzhixiang.trade.engine.v1.dao.OrderMapper;
import com.yuanzhixiang.trade.engine.v1.dao.SymbolMapper;
import com.yuanzhixiang.trade.engine.v1.dao.TradeVoucherMapper;
import com.yuanzhixiang.trade.engine.v1.dao.WalletMapper;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;
import com.yuanzhixiang.trade.engine.v1.model.po.OrderPO;
import com.yuanzhixiang.trade.engine.v1.model.po.SymbolPO;
import com.yuanzhixiang.trade.engine.v1.model.po.TradeVoucherPO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import com.yuanzhixiang.trade.engine.v1.service.HoldPositionService;
import com.yuanzhixiang.trade.engine.v1.service.OrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 14:43:16
 */
@Slf4j
@Service
public class LiquidateService implements EventHandler<VoucherEvent> {

    @Autowired
    private TradeVoucherMapper tradeVoucherMapper;

    @Autowired
    private TradeVoucherConverter tradeVoucherConverter;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HoldPositionService holdPositionService;

    @Autowired
    private HoldPositionMapper holdPositionMapper;

    @Autowired
    private SymbolMapper symbolMapper;

    @Override
    @CatchException(throwException = false)
    @OptimisticLockerWhile
    @Transactional(rollbackFor = RuntimeException.class)
    public void onEvent(VoucherEvent event, long sequence, boolean endOfBatch) {
        TradeVoucherValObj tradeVoucherValObj = event.getTradeVoucherValObj();
        if (tradeVoucherValObj != null) {
            // 处理成交的单子
            doTradeVoucher(tradeVoucherValObj);
        }

        CancelVoucherValObj cancelVoucherValObj = event.getCancelVoucherValObj();
        if (cancelVoucherValObj != null) {
            // 处理撤销的单子
            doCancelVoucher(cancelVoucherValObj);
        }
    }

    private void doCancelVoucher(CancelVoucherValObj cancelVoucherValObj) {
        // 更新撤单的数量
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            OrderPO orderPO = orderService.selectOrderById(cancelVoucherValObj.getOrderId());
            orderPO.setCancelAmount(cancelVoucherValObj.getRemainQuantity());

            if (OrderSideEnum.isBuyOrder(orderPO.getSide())) {
                doCancelBuyVoucher(cancelVoucherValObj, orderPO);
            } else {
                doCancelSellVoucher(cancelVoucherValObj, orderPO);
            }

            return orderMapper.updateById(orderPO) == 1;
        });
    }

    private void doCancelSellVoucher(CancelVoucherValObj cancelVoucherValObj, OrderPO orderPO) {
        // 卖单撤单需要解冻持仓
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            HoldPositionPO holdPositionPO =
                holdPositionService.selectHoldPosition(orderPO.getUserId(), orderPO.getSymbol());

            holdPositionPO.setFrozenSellAmount(
                holdPositionPO.getFrozenSellAmount() - cancelVoucherValObj.getRemainQuantity());
            holdPositionPO.setPositionAmount(
                holdPositionPO.getPositionAmount() + cancelVoucherValObj.getRemainQuantity());
            return holdPositionMapper.updateById(holdPositionPO) == 1;
        });
    }

    private void doCancelBuyVoucher(CancelVoucherValObj cancelVoucherValObj, OrderPO orderPO) {
        // 买单撤单需要解冻资金
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            LambdaQueryWrapper<WalletPO> walletWrapper = new LambdaQueryWrapper<>();
            walletWrapper.eq(WalletPO::getUserId, orderPO.getUserId());
            WalletPO walletPO = walletMapper.selectOne(walletWrapper);

            double cancelFund = cancelVoucherValObj.getRemainQuantity() * orderPO.getPrice();
            walletPO.setFrozenFund(walletPO.getFrozenFund() - cancelFund);
            walletPO.setBalance(walletPO.getBalance() + cancelFund);
            return walletMapper.updateById(walletPO) == 1;
        });

    }

    private void doTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 交易凭证入库
            TradeVoucherPO tradeVoucherPO = tradeVoucherConverter.boToPo(tradeVoucherValObj);
            tradeVoucherMapper.insert(tradeVoucherPO);

            // 更新委托单中的交易数量
            OrderPO makerOrder = orderService.selectOrderById(tradeVoucherPO.getMakerId());
            OrderPO takeOrder = orderService.selectOrderById(tradeVoucherPO.getTakeId());

            // 转账
            doTransferFund(tradeVoucherPO, makerOrder, takeOrder);

            // 转移头寸
            doTransferPosition(tradeVoucherPO, makerOrder, takeOrder);

            // 更新委托单中的成交数量
            makerOrder.setTradeAmount(makerOrder.getTradeAmount() + tradeVoucherPO.getAmount());
            takeOrder.setTradeAmount(takeOrder.getTradeAmount() + tradeVoucherPO.getAmount());

            // 保存最新价
            updateSymbolLatestPrice(tradeVoucherValObj);

            return orderMapper.updateById(makerOrder) == 1 && orderMapper.updateById(takeOrder) == 1;
        });

    }

    private void updateSymbolLatestPrice(TradeVoucherValObj tradeVoucherValObj) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 查询之前的价格
            LambdaQueryWrapper<SymbolPO> symbolWrapper = new LambdaQueryWrapper<>();
            symbolWrapper.eq(SymbolPO::getSymbol, tradeVoucherValObj.getSymbolId());
            SymbolPO symbolPO = symbolMapper.selectOne(symbolWrapper);

            // 更新为新的价格
            // todo 更换为新的仓储
//            symbolPO.setSymbol(tradeVoucherBO.getSymbolId());
            symbolPO.setLatestPrice(tradeVoucherValObj.getPrice());
            return symbolMapper.update(symbolPO, symbolWrapper) == 1;
        });

    }

    /**
     * 转移头寸
     *
     * @param tradeVoucherPO 交易凭证
     * @param makerOrder     挂单的委托单
     * @param takeOrder      吃单的委托单
     */
    private void doTransferPosition(TradeVoucherPO tradeVoucherPO, OrderPO makerOrder,
        OrderPO takeOrder) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 获取持仓
            HoldPositionPO makerHoldPositionPO = queryHoldPosition(makerOrder);

            HoldPositionPO takeHoldPositionPO;

            if (makerOrder.getUserId().equals(takeOrder.getUserId())) {
                // 如果是自己倒手则无需再查询数据库
                takeHoldPositionPO = makerHoldPositionPO;
            } else {
                takeHoldPositionPO = queryHoldPosition(takeOrder);
            }

            HoldPositionPO buyHoldPositionPO;
            HoldPositionPO sellHoldPositionPO;
            if (OrderSideEnum.isSellOrder(tradeVoucherPO.getTakerSide())) {
                buyHoldPositionPO = makerHoldPositionPO;
                sellHoldPositionPO = takeHoldPositionPO;
            } else {
                buyHoldPositionPO = takeHoldPositionPO;
                sellHoldPositionPO = makerHoldPositionPO;
            }

//            if (sellHoldPositionPO.getId().equals(buyHoldPositionPO.getId())) {
//                // 倒手无需重新计算成本价
//            }
//            // 增持则重新计算成本价
//            else if (buyHoldPositionPO.getId() != null) {
//                // 买方购买该标的所花的总金额，即第一次的买入金额 + 第二次的买入金额
//                double totalFund = buyHoldPositionPO.getCostPrice() *
//                    (buyHoldPositionPO.getPositionAmount() + buyHoldPositionPO.getFrozenSellAmount())
//                    + tradeVoucherPO.getPrice() * tradeVoucherPO.getAmount();
//                // 购买后买方的持仓总数
//                long totalAmount = buyHoldPositionPO.getPositionAmount() + tradeVoucherPO.getAmount();
//                double costPrice =
//                    new BigDecimal(totalFund)
//                        .divide(new BigDecimal(totalAmount), 2, RoundingMode.HALF_UP)
//                        .doubleValue();
//                buyHoldPositionPO.setCostPrice(costPrice);
//            } else {
//                // 买方之前没有持仓，那么成本价就是当前成交的价格
//                buyHoldPositionPO.setCostPrice(tradeVoucherPO.getPrice());
//            }

            // todo 成本价计算方式有问题，暂时设置为 1
            buyHoldPositionPO.setCostPrice(1D);
            sellHoldPositionPO.setCostPrice(1D);

            // 转移持仓
            sellHoldPositionPO
                .setFrozenSellAmount(
                    sellHoldPositionPO.getFrozenSellAmount() - tradeVoucherPO.getAmount());
            buyHoldPositionPO
                .setPositionAmount(buyHoldPositionPO.getPositionAmount() + tradeVoucherPO.getAmount());

            // 倒手的情况则保存一次即可
            if (sellHoldPositionPO.getId().equals(buyHoldPositionPO.getId())) {
                return holdPositionMapper.updateById(sellHoldPositionPO) == 1;
            }

            if (buyHoldPositionPO.getId() != null) {
                // 更新记录失败直接返回
                if (holdPositionMapper.updateById(buyHoldPositionPO) == 0) {
                    return false;
                }
            } else {
                // 单线程且只有这个地方会给插入一条买方的持仓记录，不会出现并发问题
                holdPositionMapper.insert(buyHoldPositionPO);
            }

            if (sellHoldPositionPO.getPositionAmount() + sellHoldPositionPO.getFrozenSellAmount() == 0) {
                // 如果卖方清仓了，那么则删除持仓记录
                // 单线程且只有这里会删除记录，不会出现并发问题
                holdPositionMapper.deleteById(sellHoldPositionPO.getId());
            } else {
                // 更新记录失败直接返回
                if (holdPositionMapper.updateById(sellHoldPositionPO) == 0) {
                    return false;
                }
            }

            // 执行到之后说明一切正常
            return true;
        });

    }

    private HoldPositionPO queryHoldPosition(OrderPO orderPO) {
        HoldPositionPO makerHoldPositionPO =
            holdPositionService.selectHoldPosition(orderPO.getUserId(), orderPO.getSymbol());
        if (makerHoldPositionPO == null) {
            makerHoldPositionPO = new HoldPositionPO();
            makerHoldPositionPO.setUserId(orderPO.getUserId());
            makerHoldPositionPO.setSymbol(orderPO.getSymbol());
            makerHoldPositionPO.setPositionAmount(0L);
        }
        return makerHoldPositionPO;
    }

    /**
     * 转账
     *
     * @param tradeVoucherPO 交易凭证
     * @param makerOrder     挂单的委托单
     * @param takeOrder      吃单的委托单
     */
    private void doTransferFund(TradeVoucherPO tradeVoucherPO, OrderPO makerOrder,
        OrderPO takeOrder) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 交易转账
            LambdaQueryWrapper<WalletPO> makerWalletWrapper = new LambdaQueryWrapper<>();
            makerWalletWrapper.eq(WalletPO::getUserId, makerOrder.getUserId());
            WalletPO makerWallet = walletMapper.selectOne(makerWalletWrapper);
            WalletPO takeWallet;

            if (makerOrder.getUserId().equals(takeOrder.getUserId())) {
                // 如果是自己给自己转账则无需再查询数据库
                takeWallet = makerWallet;
            } else {
                LambdaQueryWrapper<WalletPO> takeWalletWrapper = new LambdaQueryWrapper<>();
                takeWalletWrapper.eq(WalletPO::getUserId, takeOrder.getUserId());
                takeWallet = walletMapper.selectOne(takeWalletWrapper);
            }

            // 交易资金
            double fund = tradeVoucherPO.getPrice() * tradeVoucherPO.getAmount();

            // 确认转账方向
            WalletPO buyWallet;
            WalletPO sellWallet;
            if (OrderSideEnum.isSellOrder(tradeVoucherPO.getTakerSide())) {
                buyWallet = makerWallet;
                sellWallet = takeWallet;
            } else {
                buyWallet = takeWallet;
                sellWallet = makerWallet;
            }

            // 转账
            buyWallet.setFrozenFund(buyWallet.getFrozenFund() - fund);
            sellWallet.setBalance(sellWallet.getBalance() + fund);

            // 如果是自己转给自己则保存一次就可以了
            if (buyWallet.getUserId().equals(sellWallet.getUserId())) {
                return walletMapper.updateById(buyWallet) == 1;
            }
            // 如果是两个账户之前转账则要分别保存两个账户
            else {
                return walletMapper.updateById(buyWallet) == 1 && walletMapper.updateById(sellWallet) == 1;
            }
        });
    }
}
