package com.yuanzhixiang.trade.engine.trade.web.config.springmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.databind.CustomDataBind;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.exception.GlobalExceptionHandler;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper.LocalDateDeserializer;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper.LocalDateSerializer;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper.LocalDateTimeDeserializer;
import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 11:32:16
 */
public class SpringMVCAutoConfiguration {

    /**
     * 注册 hibernate validator bean
     *
     * @return hibernate validator bean
     */
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setProviderClass(HibernateValidator.class);
        return localValidatorFactoryBean;
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public CustomDataBind customDataBind() {
        return new CustomDataBind();
    }

    @Bean
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}
