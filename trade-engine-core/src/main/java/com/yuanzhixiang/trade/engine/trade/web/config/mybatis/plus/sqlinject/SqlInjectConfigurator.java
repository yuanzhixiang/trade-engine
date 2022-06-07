package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.sqlinject;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;

/**
 * 配置自定义的 sql 注入器
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/02 14:02
 */
public class SqlInjectConfigurator extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {

        // 这里之所以先移除再添加是为了适应后面的版本，防止后面版本加方法而这里无法直接继承到
        List<AbstractMethod> methodList = super.getMethodList(mapperClass)
            .stream()
            // 过滤默认的 deleteById 方法实现
            .filter(abstractMethod -> !abstractMethod.getClass().equals(DeleteById.class))
            .collect(Collectors.toList());

        methodList.add(new DeleteByIdMethod());

        return methodList;
    }
}
