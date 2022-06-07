package com.yuanzhixiang.trade.engine.v1.model.bo;

import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 15:11:03
 */
@Data
public class SymbolBO {
    /**
     * 持有标的
     */
    private String symbol;
    /**
     * 最新价格
     */
    private Double latestPrice;
}
