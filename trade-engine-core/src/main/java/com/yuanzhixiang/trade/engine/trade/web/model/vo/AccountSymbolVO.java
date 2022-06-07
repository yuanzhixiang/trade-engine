package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/24 16:07
 */
@Data
public class AccountSymbolVO {

    /**
     * 交易标的名称
     */
    private String symbol;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 持有头寸数量
     */
    private Long quantity;
    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenQuantity;
}
