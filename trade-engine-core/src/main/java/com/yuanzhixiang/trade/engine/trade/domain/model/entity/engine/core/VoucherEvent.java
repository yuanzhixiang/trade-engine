package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;

import lombok.Data;

/**
 * 该事件用于交易引擎将交易后得到的交易凭证传递给需要处理的服务
 *
 * @author zhixiang.yuan
 * @since 2021/02/15 16:09:33
 */
@Data
public class VoucherEvent {

    /**
     * 交易凭证
     */
    private TradeVoucherValObj tradeVoucherValObj;
    /**
     * 撤销凭证
     */
    private CancelVoucherValObj cancelVoucherValObj;

    public void clear() {
        tradeVoucherValObj = null;
        cancelVoucherValObj = null;
    }
}
