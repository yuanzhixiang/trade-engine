package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.exception.OptimisticLockerException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 22:59
 */
@Slf4j
@Aspect
public class OptimisticLockerAnnotationHandler implements Ordered {

    @Pointcut("@annotation(com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerWhile)")
    public void optimisticLockerWhileAnnotation() {

    }

    @Around("optimisticLockerWhileAnnotation()")
    public Object optimisticLockerWhileAnnotationHandle(ProceedingJoinPoint point) throws Throwable {
        int count = 1;
        while (true) {
            try {
                return point.proceed();
            } catch (OptimisticLockerException e) {
                if (count % 10 == 0) {
                    log.error("第 [{}] 次出现乐观锁异常", count);
                }

                if (count == 100) {
                    // 如果循环 100 次还是失败则直接抛出异常
                    throw new OptimisticLockerException("乐观锁重试达到 100 次");
                }
            }

            count++;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
