package com.yuanzhixiang.trade.engine.trade.web.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 23:35
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CatchException {
    boolean throwException() default true;
}
