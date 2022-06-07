package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/06/24 22:59:27
 */
@Data
public class TradeOrderParam {

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
}
