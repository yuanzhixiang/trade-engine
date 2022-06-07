package com.yuanzhixiang.trade.engine.v1.model.bo;

import lombok.Data;

/**
 * 持仓
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 11:42:09
 */
@Data
public class HoldPositionBO {

    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 持有标的
     */
    private String symbol;
    /**
     * 持有头寸数量
     */
    private Long positionAmount;
    /**
     * 成本价
     */
    private Double costPrice;
    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenSellAmount;
}
