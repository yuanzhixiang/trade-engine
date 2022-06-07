package com.yuanzhixiang.trade.engine.v1.converter;

import com.yuanzhixiang.trade.engine.trade.web.converter.WebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.OrderBO;
import com.yuanzhixiang.trade.engine.v1.model.po.OrderPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderVO;
import org.mapstruct.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/14 21:45:03
 */
@Mapper(componentModel = "spring")
public interface OrderWebConverter extends WebConverter<OrderVO, OrderBO, OrderPO> {
}
