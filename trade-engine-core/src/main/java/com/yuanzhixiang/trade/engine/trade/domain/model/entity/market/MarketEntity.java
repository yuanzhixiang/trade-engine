package com.yuanzhixiang.trade.engine.trade.domain.model.entity.market;

import com.lmax.disruptor.EventHandler;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.VoucherEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 行情实体，无状态实体
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 19:41:52
 */
@Component
public class MarketEntity implements EventHandler<VoucherEvent> {

    @Autowired
    private List<AbstractMarketKLineEntity> kLineEntityList;

    @Override
    public void onEvent(VoucherEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getTradeVoucherValObj() != null) {
            for (AbstractMarketKLineEntity marketKLineEntity : kLineEntityList) {
                marketKLineEntity.postKLine(event.getTradeVoucherValObj());
            }
        }
    }

}
