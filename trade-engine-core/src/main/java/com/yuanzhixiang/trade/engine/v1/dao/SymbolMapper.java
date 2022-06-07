package com.yuanzhixiang.trade.engine.v1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.SymbolPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 15:06:56
 */
@Mapper
public interface SymbolMapper extends BaseMapper<SymbolPO> {
}
