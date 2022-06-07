package com.yuanzhixiang.trade.engine.trade.domain.model.entity.market;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 21:02
 */
@Component
public class FiveSecondMarketKLineEntity extends AbstractMarketKLineEntity {
    @Override
    protected MarketKLineLevel marketKLineLevel() {
        return MarketKLineLevel.FIVE_SECOND;
    }
}
