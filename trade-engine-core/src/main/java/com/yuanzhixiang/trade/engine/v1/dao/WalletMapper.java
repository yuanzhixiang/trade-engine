package com.yuanzhixiang.trade.engine.v1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 14:45:36
 */
@Mapper
public interface WalletMapper extends BaseMapper<WalletPO> {
}
