package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator;


import javax.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 11:58:55
 */
@Slf4j
public class IsEnumValidator extends AbstractIsEnumValidator<String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return enumNameSet.contains(value);
    }

}
