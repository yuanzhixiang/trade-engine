package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/10 10:55
 */
@Data
public class TradeSymbolParam extends PageParam {

    /**
     * 标的编码
     */
    private String symbol;

}
