package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order;

/**
 * 交易动作
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 15:05:55
 */
public enum OrderActionEnum {
    /**
     * 下单
     */
    CREATE,
    /**
     * 撤单
     */
    CANCEL;

    public static boolean isCreate(String action) {
        return CREATE.name().equals(action);
    }

    public static boolean isCreate(OrderActionEnum action) {
        return CREATE.equals(action);
    }

    public static boolean isCancel(OrderActionEnum action) {
        return CANCEL.equals(action);
    }
}
