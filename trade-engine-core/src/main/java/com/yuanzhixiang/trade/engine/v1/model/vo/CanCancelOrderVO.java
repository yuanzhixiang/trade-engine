package com.yuanzhixiang.trade.engine.v1.model.vo;

import java.time.LocalDateTime;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;

import lombok.Data;

/**
 * 可撤销的委托单
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 13:03:26
 */
@Data
public class CanCancelOrderVO {
    /**
     * 委托单 id
     */
    private Long orderId;
    /**
     * 标的编号
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
     * 交易方向，{@link OrderSideEnum}
     */
    private String side;
    /**
     * 成交的数量
     */
    private Long tradeAmount;
    /**
     * 委托时间
     */
    private LocalDateTime createTime;
}
