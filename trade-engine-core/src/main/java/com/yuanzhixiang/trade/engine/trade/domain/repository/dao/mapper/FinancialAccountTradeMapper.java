package com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper;


import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountTradePO;

/**
 * 交易账号 Mapper
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 19:29
 */
@Mapper
public interface FinancialAccountTradeMapper extends BaseMapper<FinancialAccountTradePO> {

}
