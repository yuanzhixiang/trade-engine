package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.exception;

/**
 * 乐观锁异常
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/18 22:47
 */
public class OptimisticLockerException extends RuntimeException {

    public OptimisticLockerException() {
        super();
    }

    public OptimisticLockerException(String message) {
        super(message);
    }
}
