package com.yuanzhixiang.trade.engine.trade.domain.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountTradeParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountTradePO;

import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialAccountTradeMapper;

/**
 * 交易账户存储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 15:43
 */
@Repository
public class AccountTradeRepository {

    @Autowired
    private FinancialAccountTradeMapper financialAccountTradeMapper;

    /**
     * 查询交易账户信息
     *
     * @param accountTradeParam 查询条件
     * @return 交易账户
     */
    public Optional<FinancialAccountTradePO> getAccountTradeOptional(AccountTradeParam accountTradeParam) {
        return Optional.ofNullable(getAccountTrade(accountTradeParam));
    }

    /**
     * 查询交易账户信息
     *
     * @param accountTradeParam 查询条件
     * @return 交易账户
     */
    public FinancialAccountTradePO getAccountTrade(AccountTradeParam accountTradeParam) {
        LambdaQueryWrapper<FinancialAccountTradePO> condition = new LambdaQueryWrapper<>();
        condition.eq(accountTradeParam.getId() != null,
            FinancialAccountTradePO::getId, accountTradeParam.getId());
        condition.eq(accountTradeParam.getAccountId() != null,
            FinancialAccountTradePO::getAccountId, accountTradeParam.getAccountId());
        return financialAccountTradeMapper.selectOne(condition);
    }

    /**
     * 创建交易账户
     *
     * @return 交易账户信息
     */
    public FinancialAccountTradePO postAccountTrade(Long accountId) {
        FinancialAccountTradePO financialAccountTradePO = new FinancialAccountTradePO();
        financialAccountTradePO.setAccountId(accountId);
        financialAccountTradePO.setBalance(0D);
        financialAccountTradePO.setFrozenFund(0D);
        financialAccountTradeMapper.insert(financialAccountTradePO);
        return financialAccountTradeMapper.selectById(financialAccountTradePO.getId());
    }

    /**
     * 修改余额和冻结资金
     *
     * @param id         交易账户 id
     * @param balance    余额
     * @param frozenFund 冻结金额
     * @param version    乐观锁
     * @return true 修改成功/ false 修改失败
     */
    public boolean putBalanceAndFrozenFund(Long id, Double balance, Double frozenFund, Long version) {
        FinancialAccountTradePO financialAccountTradePO = new FinancialAccountTradePO();
        financialAccountTradePO.setId(id);
        financialAccountTradePO.setBalance(balance);
        financialAccountTradePO.setFrozenFund(frozenFund);
        financialAccountTradePO.setVersion(version);
        return financialAccountTradeMapper.updateById(financialAccountTradePO) == 1;
    }

    /**
     * 删除该交易账户
     *
     * @param id 主键
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteById(Long id) {
        return financialAccountTradeMapper.deleteById(id) == 1;
    }
}
