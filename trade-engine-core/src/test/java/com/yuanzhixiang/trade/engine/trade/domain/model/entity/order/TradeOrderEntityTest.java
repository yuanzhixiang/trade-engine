package com.yuanzhixiang.trade.engine.trade.domain.model.entity.order;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeOrderEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

/**
 * 委托单单元测试
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 10:24
 */
public class TradeOrderEntityTest extends BaseApplication {

    @Autowired
    private TradeOrderEntityFactory tradeOrderEntityFactory;

    @Test
    public void _0001() {
        TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
        tradeOrderValObj.setAccountTradeId(0L);
        tradeOrderValObj.setSymbolId(0L);
        tradeOrderValObj.setQuantity(0L);
        tradeOrderValObj.setPrice(0.0D);
        tradeOrderValObj.setSide(OrderSideEnum.BUY.name());
        tradeOrderValObj.setType(OrderTypeEnum.LIMIT.name());

        TradeOrderEntity tradeOrderEntity = tradeOrderEntityFactory.postTradeOrder(tradeOrderValObj);
    }
}
