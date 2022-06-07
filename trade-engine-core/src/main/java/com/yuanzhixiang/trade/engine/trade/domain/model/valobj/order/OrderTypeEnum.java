package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order;

/**
 * 交易类型
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 15:06:57
 */
public enum OrderTypeEnum {
    /**
     * 限价
     */
    LIMIT,
    /**
     * 限价-即时成交剩余撤销
     */
    LIMIT_IOC,
    /**
     * 市价-即时成交剩余撤销
     */
    MARKET,
    /**
     * 市价-最优五档即时成交剩余撤销
     */
    MARKET_TOP_5,
    /**
     * 市价-最优十档即时成交剩余撤销
     */
    MARKET_TOP_10,
    /**
     * 市价-对手方最优价
     */
    MARKET_OPPONENT
}
