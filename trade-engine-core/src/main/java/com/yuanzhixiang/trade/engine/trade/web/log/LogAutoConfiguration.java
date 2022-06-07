package com.yuanzhixiang.trade.engine.trade.web.log;

import org.springframework.context.annotation.Bean;

import com.yuanzhixiang.trade.engine.trade.web.log.annotation.CatchExceptionHandler;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 23:42
 */
public class LogAutoConfiguration {

    @Bean
    public CatchExceptionHandler catchExceptionHandler() {
        return new CatchExceptionHandler();
    }
}
