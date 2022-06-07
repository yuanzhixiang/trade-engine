package com.yuanzhixiang.trade.engine.v1.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.UserPO;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 16:42:47
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
