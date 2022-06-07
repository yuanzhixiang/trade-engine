package com.yuanzhixiang.trade.engine.trade.web.log.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 23:35
 */
@Slf4j
@Aspect
public class CatchExceptionHandler implements Ordered {

    @Pointcut("@annotation(com.yuanzhixiang.trade.engine.trade.web.log.annotation.CatchException)")
    public void catchExceptionAnnotation() {

    }

    @Around("catchExceptionAnnotation() && @annotation(catchException)")
    public Object catchExceptionHandle(ProceedingJoinPoint point, CatchException catchException) throws Throwable {
        try {
            return point.proceed();
        } catch (Exception e) {
            log.error("", e);
            if (!catchException.throwException()) {
                return null;
            }

            throw e;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
