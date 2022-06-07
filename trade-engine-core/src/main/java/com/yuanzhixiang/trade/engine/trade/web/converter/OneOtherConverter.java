package com.yuanzhixiang.trade.engine.trade.web.converter;

import java.util.Collection;
import java.util.List;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/21 20:53
 */
public interface OneOtherConverter<One, Other> {

    Other oneToOther(One one);

    @InheritInverseConfiguration(name = "oneToOther")
    One otherToOne(Other other);

    @InheritConfiguration(name = "oneToOther")
    List<Other> oneToOther(Collection<One> one);

    @InheritInverseConfiguration(name = "oneToOther")
    List<One> otherToOne(Collection<Other> other);
}
