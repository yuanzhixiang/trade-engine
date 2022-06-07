package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus;

import org.springframework.context.annotation.Bean;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerAnnotationHandler;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.sqlinject.SqlInjectConfigurator;

/**
 * mybatis plus 所有的配置对象都收在这个里面管理
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/18 22:25
 */
public class MybatisPlusAutoConfiguration {

    /**
     * 注册插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 处理乐观锁的注解
     */
    @Bean
    public OptimisticLockerAnnotationHandler optimisticLockerAnnotationHandler() {
        return new OptimisticLockerAnnotationHandler();
    }

    /**
     * 自定义 sql 注入期配置对象
     */
    @Bean
    public SqlInjectConfigurator sqlInjectConfigurator() {
        return new SqlInjectConfigurator();
    }

}
