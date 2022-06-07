package com.yuanzhixiang.trade.engine.trade.domain.repository;

import org.springframework.stereotype.Repository;

import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialTradeVoucherPO;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialTradeVoucherMapper;

/**
 * 交易凭证仓储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 14:12
 */
@Repository
public class TradeVoucherRepository {

    private final FinancialTradeVoucherMapper financialTradeVoucherMapper;

    public TradeVoucherRepository(
        FinancialTradeVoucherMapper financialTradeVoucherMapper) {
        this.financialTradeVoucherMapper = financialTradeVoucherMapper;
    }

    /**
     * 创建交易凭证
     *
     * @param tradeVoucherValObj 交易凭证值对象
     */
    public void postTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        FinancialTradeVoucherPO financialTradeVoucherPO = new FinancialTradeVoucherPO();
        financialTradeVoucherPO.setMakerId(tradeVoucherValObj.getMakerId());
        financialTradeVoucherPO.setTakeId(tradeVoucherValObj.getTakeId());
        financialTradeVoucherPO.setTakerSide(tradeVoucherValObj.getTakerSide());
        financialTradeVoucherPO.setQuantity(tradeVoucherValObj.getQuantity());
        financialTradeVoucherPO.setPrice(tradeVoucherValObj.getPrice());
        financialTradeVoucherPO.setSymbolId(tradeVoucherValObj.getSymbolId());
        financialTradeVoucherPO.setTradeTime(tradeVoucherValObj.getTradeTime());

        financialTradeVoucherMapper.insert(financialTradeVoucherPO);
    }

}
