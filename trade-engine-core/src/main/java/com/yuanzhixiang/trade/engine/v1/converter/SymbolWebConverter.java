package com.yuanzhixiang.trade.engine.v1.converter;

import com.yuanzhixiang.trade.engine.trade.web.converter.WebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.SymbolBO;
import com.yuanzhixiang.trade.engine.v1.model.po.SymbolPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.SymbolVO;
import org.mapstruct.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 16:54:06
 */
@Mapper(componentModel = "spring")
public interface SymbolWebConverter extends WebConverter<SymbolVO, SymbolBO, SymbolPO> {
}
