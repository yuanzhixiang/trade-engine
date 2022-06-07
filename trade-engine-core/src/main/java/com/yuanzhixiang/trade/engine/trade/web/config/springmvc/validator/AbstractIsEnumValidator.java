package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import javax.validation.ConstraintValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 18:28
 */
@Slf4j
public abstract class AbstractIsEnumValidator<T> implements ConstraintValidator<IsEnum, T> {

    private Class<? extends Enum<?>> enumClass;

    protected HashSet<String> enumNameSet;

    @Override
    public void initialize(IsEnum isEnum) {
        enumClass = isEnum.enumClass();
        try {
            Method valuesMethod = enumClass.getMethod("values");
            Enum[] enumValueArr = (Enum[]) valuesMethod.invoke(null, null);
            enumNameSet = new HashSet<>();
            for (Enum anEnum : enumValueArr) {
                enumNameSet.add(anEnum.name());
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
        }
    }
}
