package com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;

/**
 * 委托单 Mapper
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 19:29
 */
@Mapper
public interface FinancialOrderMapper extends BaseMapper<FinancialOrderPO> {

}
