package com.yuanzhixiang.trade.engine.trade.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.MarketKLineParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialMarketKLinePO;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialMarketKLineMapper;

/**
 * 行情服务仓储层
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 20:01:36
 */
@Component
public class MarketKLineRepository {

    @Autowired
    private FinancialMarketKLineMapper financialMarketKLineMapper;

    /**
     * 创建 k 线
     *
     * @param financialMarketKLinePO k 线数据
     * @return 该数据主键
     */
    public Long postMarketKLine(FinancialMarketKLinePO financialMarketKLinePO) {
        financialMarketKLineMapper.insert(financialMarketKLinePO);
        return financialMarketKLinePO.getId();
    }

    /**
     * 按照 id 更新行情数据
     *
     * @param financialMarketKLinePO k 线数据
     */
    public void updateById(FinancialMarketKLinePO financialMarketKLinePO) {
        financialMarketKLineMapper.updateById(financialMarketKLinePO);
    }

    /**
     * 按条件查询最新一条 k 线
     *
     * @param symbolId 标的 id
     * @param level    k 线级别
     * @return 查询出来的 k 线
     */
    public Optional<FinancialMarketKLinePO> getMarketKLine(Long symbolId, MarketKLineLevel level) {
        LambdaQueryWrapper<FinancialMarketKLinePO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialMarketKLinePO::getLevel, level.name());
        condition.eq(FinancialMarketKLinePO::getSymbolId, symbolId);
        condition.orderByDesc(FinancialMarketKLinePO::getId);
        condition.last("LIMIT 1");
        FinancialMarketKLinePO financialMarketKLinePO = financialMarketKLineMapper.selectOne(condition);
        return Optional.ofNullable(financialMarketKLinePO);
    }

    /**
     * 按条件查询行情 k 线
     *
     * @param marketKLineParam 查询条件
     * @return 查询结果
     */
    public List<FinancialMarketKLinePO> getMarketKLineList(MarketKLineParam marketKLineParam) {
        LambdaQueryWrapper<FinancialMarketKLinePO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialMarketKLinePO::getLevel, marketKLineParam.getLevel());
        condition.eq(FinancialMarketKLinePO::getSymbolId, marketKLineParam.getSymbolId());
        condition.ge(marketKLineParam.getGeTradeTime() != null,
            FinancialMarketKLinePO::getTime, marketKLineParam.getGeTradeTime());

        if (marketKLineParam.getLastMarket()) {
            condition.last("LIMIT 1");
        }

        condition.orderByDesc(FinancialMarketKLinePO::getTime);
        return financialMarketKLineMapper.selectList(condition);
    }

}
