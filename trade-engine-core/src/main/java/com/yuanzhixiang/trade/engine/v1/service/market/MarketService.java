package com.yuanzhixiang.trade.engine.v1.service.market;

import org.springframework.stereotype.Service;

import com.lmax.disruptor.EventHandler;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.VoucherEvent;

/**
 * @author zhixiang.yuan
 * @since 2021/02/21 20:21:48
 */
@Service
public class MarketService implements EventHandler<VoucherEvent> {
    @Override
    public void onEvent(VoucherEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getTradeVoucherValObj() != null) {
            generateCandlestick(event.getTradeVoucherValObj());
        }
    }

    private void generateCandlestick(TradeVoucherValObj tradeVoucherValObj) {
        // todo 生成各种级别的蜡烛线
    }
}
