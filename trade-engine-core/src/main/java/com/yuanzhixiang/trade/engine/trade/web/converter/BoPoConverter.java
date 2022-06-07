package com.yuanzhixiang.trade.engine.trade.web.converter;

import java.util.Collection;
import java.util.List;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/21 20:27
 */
public interface BoPoConverter<BO, PO> {

    BO poToBo(PO po);

    @InheritInverseConfiguration(name = "poToBo")
    PO boToPo(BO bo);

    @InheritConfiguration(name = "poToBo")
    List<BO> poToBo(Collection<PO> po);

    @InheritInverseConfiguration(name = "poToBo")
    List<PO> boToPo(Collection<BO> bo);
}
