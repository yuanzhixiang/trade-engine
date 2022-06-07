package com.yuanzhixiang.trade.engine.v1.model.vo;

import lombok.Data;

/**
 * 交易记录
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/09 19:12
 */
@Data
public class TradeRecordVO {

    /**
     * 委托单 id
     */
    private Long id;

    /**
     * 买入的委托单
     */
    private TradeOrderVO buyOrder;

    /**
     * 买入的委托单
     */
    private TradeOrderVO sellOrder;

    /**
     * 交易的数量
     */
    private Long number;

    /**
     * 交易的价格
     */
    private Double price;



}
