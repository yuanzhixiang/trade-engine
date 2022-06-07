package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import java.util.ArrayList;
import java.util.List;

import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.TradeVoucherSender;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import lombok.Getter;

/**
 * 交易凭证发送器，这是一个专门用于测试的实现
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 13:56
 */
public class TradeVoucherSenderTrade implements TradeVoucherSender {

    /**
     * 存储产生的交易凭证
     */
    @Getter
    private List<TradeVoucherValObj> tradeVoucherValObjList = new ArrayList<>();

    /**
     * 存储产生的撤销凭证
     */
    @Getter
    private List<CancelVoucherValObj> cancelVoucherValObjList = new ArrayList<>();

    @Override
    public void submitTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        tradeVoucherValObjList.add(tradeVoucherValObj);
    }

    @Override
    public void submitCancelVoucher(CancelVoucherValObj cancelVoucherValObj) {
        cancelVoucherValObjList.add(cancelVoucherValObj);
    }


}
