package com.yuanzhixiang.trade.engine.v1.model.vo;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/09 19:15
 */
@Data
public class TradeOrderVO {

    /**
     * 委托单 id
     */
    private Long id;
    /**
     * 交易标的
     */
    private String symbol;
    /**
     * 委托数量
     */
    private Long amount;
    /**
     * 委托价格
     */
    private Double price;
    /**
     * 交易动作，{@link OrderActionEnum}
     */
    private String action;

    /**
     * 交易方向，{@link OrderSideEnum}
     */
    private String side;

    /**
     * 交易类型，{@link OrderTypeEnum}
     */
    private String type;

    /**
     * 被撤销的委托单
     */
    private Long cancelOrderId;
}
