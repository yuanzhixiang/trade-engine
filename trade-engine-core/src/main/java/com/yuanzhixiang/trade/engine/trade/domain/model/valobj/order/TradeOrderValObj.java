package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order;

import lombok.Data;

/**
 * 委托单
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/09 18:39
 */
@Data
public class TradeOrderValObj {

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 交易数量
     */
    private Long quantity;
    /**
     * 交易价格
     */
    private Double price;

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
    private Long completedQuantity;
    /**
     * 撤销的数量
     */
    private Long canceledQuantity;
}
