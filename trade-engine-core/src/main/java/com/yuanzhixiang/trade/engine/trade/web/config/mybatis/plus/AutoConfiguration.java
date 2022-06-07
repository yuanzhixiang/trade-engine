package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus;

import org.springframework.context.annotation.Bean;

/**
 * @author zhixiang.yuan
 * @since 2021/02/14 21:21:26
 */
public class AutoConfiguration {

    @Bean
    public AutoFillMetaObjectHandler autoFillMetaObjectHandler() {
        return new AutoFillMetaObjectHandler();
    }
}
