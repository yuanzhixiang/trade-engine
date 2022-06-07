package com.yuanzhixiang.trade.engine.trade.exception;

import lombok.Getter;

/**
 * 基础异常类，所有异常都得继承这个异常
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 21:31
 */
@Getter
public class BaseException extends RuntimeException {

    BaseException(String message) {
        super(message);
    }

    private final String code = ExceptionCode.FAILURE.getCode();

}
