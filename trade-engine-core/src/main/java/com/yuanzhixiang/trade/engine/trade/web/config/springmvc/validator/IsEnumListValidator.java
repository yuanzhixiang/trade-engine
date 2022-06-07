package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 18:25
 */
@Slf4j
public class IsEnumListValidator extends AbstractIsEnumValidator<List<String>> {

    @Override
    public boolean isValid(List<String> list, ConstraintValidatorContext context) {
        if (CollectionUtil.isEmpty(list)) {
            return true;
        }
        for (String str : list) {
            if (!enumNameSet.contains(str)) {
                return false;
            }
        }
        return true;
    }
}
