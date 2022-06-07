package com.yuanzhixiang.trade.engine.trade.domain.model.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountTradeParam;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountTradeRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.TradeVoucherRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountTradePO;

/**
 * 交易账户实体工厂
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 14:08
 */
@Component
public class AccountTradeEntityFactory {

    @Autowired
    private TradeVoucherRepository tradeVoucherRepository;

    @Autowired
    private AccountTradeRepository accountTradeRepository;

    /**
     * 查询交易账户实体
     *
     * @param accountTradeId 交易账户 id
     * @return 交易账户实体
     */
    public AccountTradeEntity getAccountTradeEntity(Long accountTradeId) {
        AccountTradeParam accountTradeParam = new AccountTradeParam();
        accountTradeParam.setId(accountTradeId);
        FinancialAccountTradePO financialAccountTradePO = accountTradeRepository.getAccountTradeOptional(accountTradeParam).get();
        return new AccountTradeEntity(financialAccountTradePO.getId());
    }

    /**
     * 创建交易账户实体
     *
     * @return 交易账户实体
     */
    public AccountTradeEntity postAccountTrade() {
        FinancialAccountTradePO financialAccountTradePO = accountTradeRepository.postAccountTrade(null);
        return new AccountTradeEntity(financialAccountTradePO.getId());
    }

    /**
     * 创建交易凭证
     *
     * @param tradeVoucherValObj 交易凭证值对象
     */
    public void postTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        tradeVoucherRepository.postTradeVoucher(tradeVoucherValObj);
    }
}
