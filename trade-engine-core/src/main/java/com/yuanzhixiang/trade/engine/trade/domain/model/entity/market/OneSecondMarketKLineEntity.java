package com.yuanzhixiang.trade.engine.trade.domain.model.entity.market;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;

/**
 * 一秒钟 k 线生成实体
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 20:47:43
 */
@Component
public class OneSecondMarketKLineEntity extends AbstractMarketKLineEntity {
    @Override
    protected MarketKLineLevel marketKLineLevel() {
        return MarketKLineLevel.ONE_SECOND;
    }
}
