package com.yuanzhixiang.trade.engine;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class TradeEngineServiceApplication implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TradeEngineServiceApplication.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(TradeEngineServiceApplication.class, args);
    }
}
