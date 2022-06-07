package com.yuanzhixiang.trade.engine.trade.domain.model.aggregate;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;

/**
 * 交易标的聚合根单元测试
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 08:38
 */
public class TradeSymbolAggregateTest extends BaseApplication {

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 检查无效标的的初始化状态
     */
    @Test
    public void _0001() {
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory.getTradeSymbol(
            TradeSymbolUtil.generateSymbol())
            .orElse(null);
        Assert.assertNull("无效标的应该查不出来", tradeSymbolAggregate);
    }

    /**
     * 开启撮合引擎，不报错就是正常
     */
    @Test
    public void _0004() {
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(TradeSymbolUtil.generateSymbol());
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory.postTradeSymbol(tradeSymbolValObj);

        // 开启撮合引擎
        tradeSymbolAggregate.startTradeEngine();
    }

    /**
     * 关闭撮合引擎，不报错就是正常
     */
    @Test
    public void _0005() {
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(TradeSymbolUtil.generateSymbol());
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory.postTradeSymbol(tradeSymbolValObj);

        // 开启撮合引擎
        tradeSymbolAggregate.startTradeEngine();

        // 关闭撮合引擎
        tradeSymbolAggregate.closeTradeEngine();
    }

    /**
     * 测试多次开关撮合引擎
     */
    @Test
    public void _0006() {
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(TradeSymbolUtil.generateSymbol());
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory.postTradeSymbol(tradeSymbolValObj);

        // 连续开启引擎
        tradeSymbolAggregate.startTradeEngine();

        tradeSymbolAggregate.startTradeEngine();

        // 连续关闭引擎
        tradeSymbolAggregate.closeTradeEngine();

        tradeSymbolAggregate.closeTradeEngine();

        // 开启引擎后立马关闭引擎
        tradeSymbolAggregate.startTradeEngine();

        tradeSymbolAggregate.closeTradeEngine();
    }

}
