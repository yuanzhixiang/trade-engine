package com.yuanzhixiang.trade.engine.v1.converter;

import com.yuanzhixiang.trade.engine.trade.web.converter.BoPoConverter;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.v1.model.po.TradeVoucherPO;
import org.mapstruct.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 15:40:57
 */
@Mapper(componentModel = "spring")
public interface TradeVoucherConverter extends BoPoConverter<TradeVoucherValObj, TradeVoucherPO> {
}
