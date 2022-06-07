package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 22:38
 */
@FunctionalInterface
public interface OnlyReturnPredicate {
    boolean test();
}
