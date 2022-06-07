package com.yuanzhixiang.trade.engine.v1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:23:00
 */
@Mapper
public interface HoldPositionMapper extends BaseMapper<HoldPositionPO> {
}
