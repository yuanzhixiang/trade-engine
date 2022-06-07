package com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder;

import org.mapstruct.Mapper;

import com.yuanzhixiang.trade.engine.trade.web.converter.OneOtherConverter;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.mapstruct.Constants;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/30 10:03
 */
@Mapper(componentModel = Constants.CREATE_MODEL)
public interface TradeOrderPOConverter extends OneOtherConverter<TradeOrderValObj, FinancialOrderPO> {

}
