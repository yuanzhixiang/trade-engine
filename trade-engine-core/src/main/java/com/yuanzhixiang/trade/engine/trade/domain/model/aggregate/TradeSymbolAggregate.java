package com.yuanzhixiang.trade.engine.trade.domain.model.aggregate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.yuanzhixiang.trade.engine.trade.domain.model.factory.AccountTradeEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeOrderEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeOrderParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder.TradeOrderRepository;

import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.RobotGroupEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.TradeEngineEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineStatusEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.TradeSymbolRepository;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;
import lombok.Getter;

/**
 * 交易标的聚合根
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 20:48
 */
public class TradeSymbolAggregate extends BaseAggregate {

    /**
     * 标的编码
     */
    @Getter
    private final String symbol;

    /**
     * registry 用于注册撮合引擎，由于目前是单体架构，所以直接在内存中保存该引擎，如果后面编程分布式系统则不能在这里维护状态， 这里需要加 volatile 防止编译器对指令进行优化造成空指针
     */
    private static ConcurrentHashMap<String, TradeEngineEntity> tradeEngineRegistry = new ConcurrentHashMap<>();

    private final TradeSymbolRepository tradeSymbolRepository;

    private final TradeOrderRepository tradeOrderRepository;

    private final TradeOrderEntityFactory tradeOrderEntityFactory;

    private AccountTradeEntityFactory accountTradeEntityFactory;

    public TradeSymbolAggregate(
        Long id,
        String symbol
    ) {
        super(id);
        this.symbol = symbol;
        this.tradeSymbolRepository = ApplicationContextHelper.getBean(TradeSymbolRepository.class);
        this.tradeOrderRepository = ApplicationContextHelper.getBean(TradeOrderRepository.class);
        this.tradeOrderEntityFactory = ApplicationContextHelper.getBean(TradeOrderEntityFactory.class);
        this.accountTradeEntityFactory = ApplicationContextHelper.getBean(AccountTradeEntityFactory.class);
    }

    /**
     * 校验标的编码是否符合规范
     *
     * @param symbol 标的编码
     */
    public static void checkSymbol(String symbol) {
        if (StrUtil.isBlank(symbol)) {
            ExceptionHelper.throwTradeSymbolException("交易标的编码不能为空");
        }
    }

    /**
     * 交易引擎是否开启
     *
     * @return true 开启/ false 未开启
     */
    public EngineStatusEnum getEngineStatus() {
        return tradeEngineRegistry.get(symbol) != null ? EngineStatusEnum.RUNNING : EngineStatusEnum.CLOSE;
    }

    /**
     * 开启指定标的交易引擎
     */
    public void startTradeEngine() {
        TradeEngineEntity tradeEngineEntity = tradeEngineRegistry.get(symbol);

        // 注册表中有引擎则说明引擎已经开启
        if (tradeEngineEntity != null) {
            return;
        }

        // 如果引擎未开启则去初始化引擎
        synchronized (symbol.intern()) {
            // 双重检查防止出现并发问题
            tradeEngineEntity = tradeEngineRegistry.get(symbol);
            if (tradeEngineEntity != null) {
                return;
            }

            // 开启交易引擎
            tradeEngineEntity = new TradeEngineEntity(getId());

            // 初始化未交易的委托单到引擎
            TradeOrderParam tradeOrderParam = new TradeOrderParam();
            tradeOrderParam.setSymbolId(getId());
            List<FinancialOrderPO> canCancelOrder = tradeOrderRepository.getCanCancelOrder(tradeOrderParam);
            for (FinancialOrderPO financialOrderPO : canCancelOrder) {
                tradeEngineEntity.submitTradeOrder(tradeOrderEntityFactory.getTradeOrder(financialOrderPO.getId()));
            }

            // 初始化交易机器人
            RobotGroupEntity robotGroupEntity = RobotGroupEntity.getInstance(getId());
            robotGroupEntity.startTradeTask();

            tradeEngineRegistry.put(symbol, tradeEngineEntity);
        }
    }

    /**
     * 关闭指定标的撮合引擎
     */
    public void closeTradeEngine() {
        TradeEngineEntity tradeEngineEntity = tradeEngineRegistry.get(symbol);

        // 如果没有撮合引擎则直接返回
        if (tradeEngineEntity == null) {
            return;
        }

        synchronized (symbol.intern()) {
            // 双重检查防止出现并发问题
            tradeEngineEntity = tradeEngineRegistry.get(symbol);
            if (tradeEngineEntity == null) {
                return;
            }

            // 关闭交易机器人
            RobotGroupEntity robotGroupEntity = RobotGroupEntity.getInstance(getId());
            robotGroupEntity.stopTradeTask();

            // 关闭撮合引擎
            tradeEngineEntity.close();
            tradeEngineRegistry.remove(symbol);
        }
    }

    /**
     * 查询撮合引擎
     *
     * @return 撮合引擎
     */
    public TradeEngineEntity getTradeEngineEntity() {
        return tradeEngineRegistry.get(symbol);
    }

    /**
     * 保存委托单
     *
     * @return 委托单实体
     */
    public TradeOrderEntity postTradeOrder(TradeOrderValObj tradeOrderValObj) {
        // 买卖单需要分开处理
        if (OrderSideEnum.isBuyOrder(tradeOrderValObj.getSide())) {
            return doCreateBuyOrder(tradeOrderValObj);
        } else {
            return doCreateSellOrder(tradeOrderValObj);
        }
    }

    private TradeOrderEntity doCreateSellOrder(TradeOrderValObj tradeOrderValObj) {

        // 冻结资产
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory
            .getAccountTradeEntity(tradeOrderValObj.getAccountTradeId());

        // 冻结证券
        if (!accountTradeEntity.frozenSymbolQuantity(tradeOrderValObj.getSymbolId(), tradeOrderValObj.getQuantity())) {
            ExceptionHelper.throwSystemException("冻结证券失败");
        }

        return tradeOrderEntityFactory.postTradeOrder(tradeOrderValObj);
    }

    private TradeOrderEntity doCreateBuyOrder(TradeOrderValObj tradeOrderValObj) {
        // 冻结资产
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory
            .getAccountTradeEntity(tradeOrderValObj.getAccountTradeId());

        // 冻结资金
        if (!accountTradeEntity.frozenFund(tradeOrderValObj.getQuantity() * tradeOrderValObj.getPrice())) {
            ExceptionHelper.throwSystemException("冻结资金失败");
        }

        return tradeOrderEntityFactory.postTradeOrder(tradeOrderValObj);
    }

    /**
     * 提交委托单
     *
     * @param tradeOrderValObj 委托单值对象
     */
    public void submitTradeOrder(TradeOrderValObj tradeOrderValObj) {
        if (!engineIsOpen()) {
            ExceptionHelper.throwTradeSymbolException("引擎未开启，symbol: [{}]", symbol);
        }

        TradeEngineEntity tradeEngineEntity = getTradeEngineEntity();
        tradeEngineEntity.submitTradeOrder(postTradeOrder(tradeOrderValObj));
    }

    /**
     * 引擎是否开启
     *
     * @return true 开启 / false 未开启
     */
    public boolean engineIsOpen() {
        TradeEngineEntity tradeEngineEntity = getTradeEngineEntity();
        return tradeEngineEntity != null && tradeEngineEntity.isOpen();
    }

    /**
     * 撤销委托单
     *
     * @param cancelOrderId 委托单 id
     */
    public void submitCancelOrder(Long cancelOrderId) {
        if (engineIsOpen()) {
            TradeEngineEntity tradeEngineEntity = getTradeEngineEntity();
            tradeEngineEntity.submitCancelOrder(cancelOrderId);
        } else {
            ExceptionHelper.throwTradeSymbolException("引擎未开启，symbol: [{}]", symbol);
        }
    }

    /**
     * 删除标的
     */
    public void deleteTradeSymbol() {
        // 先关引擎
        closeTradeEngine();
        // 再删标的
        tradeSymbolRepository.deleteTradeSymbol(getId());
    }
}
