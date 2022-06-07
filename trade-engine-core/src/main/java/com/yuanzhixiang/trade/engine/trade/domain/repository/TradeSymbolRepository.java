package com.yuanzhixiang.trade.engine.trade.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeSymbolParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialSymbolPO;

import cn.hutool.core.util.StrUtil;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialSymbolMapper;
import com.yuanzhixiang.trade.engine.trade.utils.PageUtil;

/**
 * 交易标的存储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 21:03
 */
@Repository
public class TradeSymbolRepository {

    private final FinancialSymbolMapper financialSymbolMapper;

    public TradeSymbolRepository(
        FinancialSymbolMapper financialSymbolMapper
    ) {
        this.financialSymbolMapper = financialSymbolMapper;
    }

    /**
     * 查询交易标的信息
     *
     * @param symbol 标的编码
     * @return 标的聚合根
     */
    public FinancialSymbolPO getTradeSymbol(String symbol) {
        TradeSymbolAggregate.checkSymbol(symbol);

        // 查询交易标的
        LambdaQueryWrapper<FinancialSymbolPO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialSymbolPO::getSymbol, symbol);
        return financialSymbolMapper.selectOne(condition);
    }

    /**
     * 查询标的信息
     *
     * @param id 标的主键
     * @return 标的聚合根
     */
    public FinancialSymbolPO getTradeSymbol(Long id) {
        return financialSymbolMapper.selectById(id);
    }

    /**
     * 查询交易标的列表
     *
     * @return 交易标的列表
     */
    public List<FinancialSymbolPO> getTradeSymbolList() {
        return financialSymbolMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 分页查询标的列表
     *
     * @return 分页查询结果
     */
    public PageResult<FinancialSymbolPO> getTradeSymbolPage(TradeSymbolParam tradeSymbolParam) {
        LambdaQueryWrapper<FinancialSymbolPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(tradeSymbolParam.getSymbol()), FinancialSymbolPO::getSymbol,
            tradeSymbolParam.getSymbol());
        return PageUtil.pageToPageResult(financialSymbolMapper
            .selectPage(PageUtil.pageParamToPage(tradeSymbolParam), wrapper));
    }

    /**
     * 新增交易标的信息
     *
     * @param tradeSymbolValObj 标的编码
     */
    public Long postTradeSymbol(TradeSymbolValObj tradeSymbolValObj) {
        FinancialSymbolPO financialSymbolPO = new FinancialSymbolPO();
        financialSymbolPO.setSymbol(tradeSymbolValObj.getSymbol());
        financialSymbolMapper.insert(financialSymbolPO);

        return financialSymbolPO.getId();
    }

    /**
     * 删除标的
     *
     * @param symbolId 标的 id
     */
    public void deleteTradeSymbol(Long symbolId) {
        financialSymbolMapper.deleteById(symbolId);
    }

}
