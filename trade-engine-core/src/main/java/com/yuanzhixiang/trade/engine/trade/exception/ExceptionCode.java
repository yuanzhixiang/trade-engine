package com.yuanzhixiang.trade.engine.trade.exception;

import lombok.Getter;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/29 21:33
 */
@Getter
public enum ExceptionCode {
    /**
     * 正常
     */
    SUCCESS("000000"),
    /**
     * 未知异常
     */
    FAILURE("999999");

    private final String code;

    ExceptionCode(String code) {
        this.code = code;
    }
}
