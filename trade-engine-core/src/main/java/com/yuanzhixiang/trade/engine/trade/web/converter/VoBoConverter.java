package com.yuanzhixiang.trade.engine.trade.web.converter;

import java.util.Collection;
import java.util.List;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/21 20:27
 */
public interface VoBoConverter<VO, BO> {

    VO boToVo(BO bo);

    @InheritInverseConfiguration(name = "boToVo")
    BO voToBo(VO vo);

    @InheritConfiguration(name = "boToVo")
    List<VO> boToVo(Collection<BO> bo);

    @InheritInverseConfiguration(name = "boToVo")
    List<BO> voToBo(Collection<VO> vo);

}
