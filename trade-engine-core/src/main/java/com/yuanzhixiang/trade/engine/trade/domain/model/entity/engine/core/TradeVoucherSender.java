package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;

/**
 * 交易凭证发送器
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 13:54
 */
public interface TradeVoucherSender {

    /**
     * 提交交易凭证
     *
     * @param tradeVoucherValObj 交易凭证
     */
    void submitTradeVoucher(TradeVoucherValObj tradeVoucherValObj);

    /**
     * 提交撤销凭证
     *
     * @param cancelVoucherValObj 撤销凭证
     */
    void submitCancelVoucher(CancelVoucherValObj cancelVoucherValObj);
}
