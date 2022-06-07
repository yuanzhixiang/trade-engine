package com.yuanzhixiang.trade.engine.trade.domain.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.HoldPositionParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialHoldPositionPO;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialHoldPositionMapper;
import com.yuanzhixiang.trade.engine.trade.utils.PageUtil;

/**
 * 持仓服务仓储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 17:27
 */
@Repository
public class HoldPositionRepository {

    @Autowired
    private FinancialHoldPositionMapper financialHoldPositionMapper;

    /**
     * 查询指定账户关于某标的的持仓
     *
     * @param accountTradeId 交易账户 id
     * @param symbolId       标的 id
     * @return 持仓记录
     */
    public Optional<FinancialHoldPositionPO> getHoldPositionOptional(Long accountTradeId, Long symbolId) {
        return Optional.ofNullable(getHoldPosition(accountTradeId, symbolId));
    }

    /**
     * 查询指定账户关于某标的的持仓
     *
     * @param accountTradeId 交易账户 id
     * @param symbolId       标的 id
     * @return 持仓记录
     */
    public FinancialHoldPositionPO getHoldPosition(Long accountTradeId, Long symbolId) {
        LambdaQueryWrapper<FinancialHoldPositionPO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialHoldPositionPO::getAccountTradeId, accountTradeId);
        condition.eq(FinancialHoldPositionPO::getSymbolId, symbolId);
        return financialHoldPositionMapper.selectOne(condition);
    }


    /**
     * 分页查询
     *
     * @param holdPositionParam 分页查询参数
     * @return 分页查询结果
     */
    public PageResult<FinancialHoldPositionPO> getHoldPositionPage(HoldPositionParam holdPositionParam) {
        LambdaQueryWrapper<FinancialHoldPositionPO> condition = paramToWrapper(holdPositionParam);
        return PageUtil.pageToPageResult(financialHoldPositionMapper
            .selectPage(PageUtil.pageParamToPage(holdPositionParam), condition));
    }

    public LambdaQueryWrapper<FinancialHoldPositionPO> paramToWrapper(HoldPositionParam holdPositionParam) {
        LambdaQueryWrapper<FinancialHoldPositionPO> condition = new LambdaQueryWrapper<>();
        condition.eq(holdPositionParam.getSymbolId() != null,
            FinancialHoldPositionPO::getSymbolId, holdPositionParam.getSymbolId());
        condition.eq(holdPositionParam.getAccountTradeId() != null,
            FinancialHoldPositionPO::getAccountTradeId, holdPositionParam.getAccountTradeId());
        return condition;
    }

    /**
     * 创建持仓记录
     *
     * @param financialHoldPositionPO 持仓记录
     * @return 持仓记录 id
     */
    public Long postHoldPosition(FinancialHoldPositionPO financialHoldPositionPO) {
        financialHoldPositionMapper.insert(financialHoldPositionPO);
        return financialHoldPositionPO.getId();
    }

    /**
     * 修改持有的标的数量
     *
     * @param id             主键
     * @param quantity       数量
     * @param frozenQuantity 冻结的数量
     * @return true 修改成功/ false 修改失败
     */
    public boolean putQuantity(Long id, Long quantity, Long frozenQuantity, Long version) {
        FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
        financialHoldPositionPO.setId(id);
        financialHoldPositionPO.setQuantity(quantity);
        financialHoldPositionPO.setFrozenQuantity(frozenQuantity);
        financialHoldPositionPO.setVersion(version);
        return financialHoldPositionMapper.updateById(financialHoldPositionPO) == 1;
    }

    /**
     * 删除持仓记录
     *
     * @param id 持仓记录 id
     * @return true 删除成功/ false 删除失败
     */
    public boolean deleteHoldPosition(Long id) {
        return financialHoldPositionMapper.deleteById(id) == 1;
    }
}
