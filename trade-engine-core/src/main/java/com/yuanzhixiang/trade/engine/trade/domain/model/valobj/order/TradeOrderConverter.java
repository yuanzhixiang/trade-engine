package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.mapstruct.Constants;

/**
 * @author ZhiXiang Yuan
 * @date 2021/02/18 23:56
 */
@Mapper(componentModel = Constants.CREATE_MODEL)
public interface TradeOrderConverter {

    TradeOrderValObj copyValObj(TradeOrderValObj tradeOrderValObj);

    /**
     * 拷贝一份值对象
     *
     * @param sourceValObj 被拷贝的值对象
     * @param targetValObj 拷贝到这个值对象上
     */
    void copyValObj(TradeOrderValObj sourceValObj, @MappingTarget TradeOrderValObj targetValObj);

    /**
     * 拷贝一份值对象
     *
     * @param sourceEntity 被拷贝的值对象
     * @param targetEntity 拷贝到这个值对象上
     */
    void copyEntity(TradeOrderEntity sourceEntity, @MappingTarget TradeOrderEntity targetEntity);
}
