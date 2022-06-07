package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market;

/**
 * K 线级别
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 20:13:03
 */
public enum MarketKLineLevel {
    /**
     * 一秒钟
     */
    ONE_SECOND(1),
    /**
     * 五秒钟
     */
    FIVE_SECOND(5),
    /**
     * 半分钟
     */
    HALF_MINUTE(30),
    /**
     * 一分钟
     */
    ONE_MINUTE(60);

    /**
     * 时间长度
     */
    private final int length;

    MarketKLineLevel(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}
