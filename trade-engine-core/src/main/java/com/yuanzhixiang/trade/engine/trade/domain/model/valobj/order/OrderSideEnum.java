package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order;

/**
 * 买卖方向
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/09 18:44
 */
public enum OrderSideEnum {
    /**
     * 买
     */
    BUY,
    /**
     * 卖
     */
    SELL;

    /**
     * 是买入订单
     *
     * @param side 方向
     * @return 是否是买入订单
     */
    public static boolean isBuyOrder(String side) {
        return BUY.name().equals(side);
    }

    public static boolean isSellOrder(String side) {
        return SELL.name().equals(side);
    }
}
