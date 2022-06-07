package com.yuanzhixiang.trade.engine.trade.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * IOC 容器工具类
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 09:00
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware {

    /**
     * 容器
     */
    private static ApplicationContext applicationContext;

    /**
     * 初始化容器对象
     *
     * @param applicationContext 容器
     * @throws BeansException spring bean 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 从容器中取出初始化过后的 Bean
     *
     * @param requiredType 需要取出的 Bean 的类型
     * @param <T>          Bean 的类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

}
