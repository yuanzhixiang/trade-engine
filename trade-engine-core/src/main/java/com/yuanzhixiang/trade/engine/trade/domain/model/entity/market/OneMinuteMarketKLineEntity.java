package com.yuanzhixiang.trade.engine.trade.domain.model.entity.market;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/26 19:37
 */
@Component
public class OneMinuteMarketKLineEntity extends AbstractMarketKLineEntity {

    @Override
    protected MarketKLineLevel marketKLineLevel() {
        return MarketKLineLevel.ONE_MINUTE;
    }
}
