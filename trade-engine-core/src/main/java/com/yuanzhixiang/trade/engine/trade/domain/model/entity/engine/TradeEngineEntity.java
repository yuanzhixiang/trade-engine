package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineStatusEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 撮合引擎实体
 *
 * @author zhixiang.yuan
 * @since 2021/02/13 16:51:40
 */
@Slf4j
public class TradeEngineEntity {

    /**
     * 引擎状态
     */
    private EngineStatusEnum engineStatusEnum;

    /**
     * 委托单发送器
     */
    private final TradeOrderSender tradeOrderSender;

    public TradeEngineEntity(Long symbolId) {
        // 初始化委托单发送器
        tradeOrderSender = new TradeOrderSender(symbolId);

        // 初始化引擎状态
        engineStatusEnum = EngineStatusEnum.RUNNING;
    }

    /**
     * 委托单是否开启
     *
     * @return true 开启/ false 未开启
     */
    public boolean isOpen() {
        return engineStatusEnum == EngineStatusEnum.RUNNING;
    }

    /**
     * 关闭引擎
     */
    public synchronized void close() {
        if (engineStatusEnum == EngineStatusEnum.RUNNING) {
            // 关闭委托单发送器
            tradeOrderSender.closeSender();

            // 修改引擎状态
            engineStatusEnum = EngineStatusEnum.CLOSE;
        }
    }

    /**
     * 提交委托单
     *
     * @param tradeOrderEntity 委托单值对象
     */
    public void submitTradeOrder(TradeOrderEntity tradeOrderEntity) {
        tradeOrderSender.submitTradeOrder(tradeOrderEntity);
    }

    /**
     * 提交撤销委托单
     *
     * @param cancelOrderId 需要被撤销的委托单的 id
     */
    public void submitCancelOrder(Long cancelOrderId) {
        tradeOrderSender.submitCancelOrder(cancelOrderId);
    }
}
