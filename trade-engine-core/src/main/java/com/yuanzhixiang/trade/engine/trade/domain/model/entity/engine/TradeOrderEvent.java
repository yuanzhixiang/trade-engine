package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineOrderValObj;

import lombok.Data;

/**
 * 该事件用于在交易引擎中传递委托单
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 10:18
 */
@Data
public class TradeOrderEvent {

    /**
     * 交易动作枚举
     */
    private OrderActionEnum orderAction;
    /**
     * 送入引擎的委托单
     */
    private EngineOrderValObj engineOrderValObj;
    /**
     * 撤销的委托单号
     */
    private Long cancelOrderId;

    public void clear() {
        engineOrderValObj = null;
        cancelOrderId = null;
        orderAction = null;
    }
}
