package com.yuanzhixiang.trade.engine.trade.application.converter;

import org.mapstruct.Mapper;

import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeSymbolParam;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.mapstruct.Constants;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeSymbolGetPageRequest;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/10 11:16
 */
@Mapper(componentModel = Constants.CREATE_MODEL)
public interface TradeSymbolApplicationConverter {

    TradeSymbolParam symbolGetRequestToParam(TradeSymbolGetPageRequest tradeSymbolGetPageRequest);
}
