package com.yuanzhixiang.trade.engine.v1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.OrderPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/14 21:34:33
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {
}
