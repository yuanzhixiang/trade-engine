package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.exception.OptimisticLockerException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 22:36
 */
@Slf4j
public class OptimisticLockerKit {

    /**
     * 判断是否更新是否正常，如果不正常则触发异常
     *
     * @param predicate 将要执行的方法
     */
    public static void doOptimisticLockExecute(OnlyReturnPredicate predicate) {
        if (!predicate.test()) {
            throw new OptimisticLockerException();
        }
    }
}
