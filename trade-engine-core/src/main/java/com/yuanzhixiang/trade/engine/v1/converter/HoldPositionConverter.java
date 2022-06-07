package com.yuanzhixiang.trade.engine.v1.converter;

import com.yuanzhixiang.trade.engine.trade.web.converter.WebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.HoldPositionBO;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;
import org.mapstruct.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:48:28
 */
@Mapper(componentModel = "spring")
public interface HoldPositionConverter
    extends WebConverter<HoldPositionVO, HoldPositionBO, HoldPositionPO> {
}
