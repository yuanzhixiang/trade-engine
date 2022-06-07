package com.yuanzhixiang.trade.engine.trade.domain.model.util;

import java.util.UUID;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;

/**
 * 交易标的工具类
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 21:04
 */
public class TradeSymbolUtil {

    /**
     * 生成唯一交易标的编码
     *
     * @return 标的编码
     */
    public static String generateSymbol() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成唯一交易标的 id
     *
     * @return 标的 id
     */
    public static Long generateSymbolId() {
        TradeSymbolAggregateFactory tradeSymbolAggregateFactory = ApplicationContextHelper.getBean(TradeSymbolAggregateFactory.class);
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(generateSymbol());
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory.postTradeSymbol(tradeSymbolValObj);
        return tradeSymbolAggregate.getId();
    }

}
