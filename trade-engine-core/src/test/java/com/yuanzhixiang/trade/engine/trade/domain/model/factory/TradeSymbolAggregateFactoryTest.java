package com.yuanzhixiang.trade.engine.trade.domain.model.factory;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.exception.TradeSymbolException;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/29 22:26
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class TradeSymbolAggregateFactoryTest extends BaseApplication {

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 查询无效交易标的时抛出异常
     */
    @Test
    public void _0001() {
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory
            // 这里 TradeSymbolUtil.generateSymbolId() 创建了一个新的聚合根，但其 id + 1 一定未被使用
            .getTradeSymbol(TradeSymbolUtil.generateSymbolId() + 1).orElse(null);
        Assert.assertNull("无效标的不应该查出聚合根", tradeSymbolAggregate);
    }

    /**
     * 交易标的编码不能为空
     */
    @Test(expected = TradeSymbolException.class)
    public void _0003() {
        tradeSymbolAggregateFactory.getTradeSymbol("");
    }
}
