package com.yuanzhixiang.trade.engine.v1.model.bo;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;

import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/14 21:52:01
 */
@Data
public class OrderBO {
    /**
     * 订单 id
     */
    private Long id;
    /**
     * 用户 id
     */
    private Long userId;
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
     * 成交的数量
     */
    private Long tradeAmount;

    /**
     * 撤销数量
     */
    private Long cancelAmount;


    /**
     * 被撤销的委托单
     */
    private Long cancelOrderId;

}
