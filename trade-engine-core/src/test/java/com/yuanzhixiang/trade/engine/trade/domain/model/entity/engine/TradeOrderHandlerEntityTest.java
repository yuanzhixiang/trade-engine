package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeOrderEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.TradeOrderHandlerEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;

/**
 * 委托单撮合引擎实体单元测试
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 11:10
 */
public class TradeOrderHandlerEntityTest extends BaseApplication {

    @Autowired
    private TradeOrderEntityFactory tradeOrderEntityFactory;

    /**
     * 创建能送入引擎的委托单
     *
     * @return 能送入引擎的委托单
     */
    private EngineOrderValObj createEngineOrderValObj(
        OrderSideEnum sideEnum, Long accountTradeId, Long symbolId, Long amount, Double price
    ) {
        TradeOrderValObj sellOrder = new TradeOrderValObj();
        sellOrder.setAccountTradeId(accountTradeId);
        sellOrder.setSymbolId(symbolId);
        sellOrder.setQuantity(amount);
        sellOrder.setPrice(price);
        sellOrder.setSide(sideEnum.name());
        sellOrder.setType(OrderTypeEnum.LIMIT.name());

        TradeOrderEntity sellOrderEntity = tradeOrderEntityFactory.postTradeOrder(sellOrder);

        return TradeOrderSender.buildEngineOrderValObj(sellOrderEntity);
    }

    /**
     * 撮合一笔单子
     */
    @Test
    public void _0001() {
        Long symbolId = TradeSymbolUtil.generateSymbolId();
        TradeVoucherSenderTrade tradeVoucherSenderTrade = new TradeVoucherSenderTrade();
        List<TradeVoucherValObj> tradeVoucherValObjList = tradeVoucherSenderTrade.getTradeVoucherValObjList();
        TradeOrderHandlerEntity tradeOrderHandlerEntity =
            new TradeOrderHandlerEntity(symbolId, tradeVoucherSenderTrade);

        // 创建买入委托单
        EngineOrderValObj buyOrder = createEngineOrderValObj(OrderSideEnum.BUY,
            0L, symbolId, 10000L, 10D);

        // 提交买入委托单
        tradeOrderHandlerEntity.doTrade(buyOrder);

        Assert.assertEquals("只放入一张买入委托单不应该产生交易凭证", 0, tradeVoucherValObjList.size());

        // 创建卖出委托单
        EngineOrderValObj sellOrder = createEngineOrderValObj(OrderSideEnum.SELL,
            0L, symbolId, 10000L, 10D);

        // 提交卖出委托单
        tradeOrderHandlerEntity.doTrade(sellOrder);

        // 应该产生一张吃单方向为 SELL 的交易凭证
        Assert.assertEquals(1, tradeVoucherValObjList.size());
        TradeVoucherValObj tradeVoucherValObj = tradeVoucherValObjList.get(0);
        Assert.assertEquals(sellOrder.getSymbolId(), tradeVoucherValObj.getSymbolId());
        Assert.assertEquals(buyOrder.getId(), tradeVoucherValObj.getMakerId());
        Assert.assertEquals(sellOrder.getId(), tradeVoucherValObj.getTakeId());
        Assert.assertEquals(sellOrder.getSide(), tradeVoucherValObj.getTakerSide());
        Assert.assertEquals(sellOrder.getQuantity(), tradeVoucherValObj.getQuantity());
        Assert.assertEquals(buyOrder.getPrice(), tradeVoucherValObj.getPrice());
    }

    /**
     * 模拟连续竞价
     */
    @Test
    public void _0002() {
        Long symbolId = TradeSymbolUtil.generateSymbolId();
        TradeVoucherSenderTrade tradeVoucherSenderTrade = new TradeVoucherSenderTrade();
        List<TradeVoucherValObj> tradeVoucherValObjList = tradeVoucherSenderTrade.getTradeVoucherValObjList();
        List<CancelVoucherValObj> cancelVoucherValObjList = tradeVoucherSenderTrade.getCancelVoucherValObjList();
        TradeOrderHandlerEntity tradeOrderHandlerEntity =
            new TradeOrderHandlerEntity(symbolId, tradeVoucherSenderTrade);

        // 开始竞价，1000 股买入价格 30 挂单
        EngineOrderValObj buyOrder1 = createEngineOrderValObj(OrderSideEnum.BUY,
            0L, symbolId, 1000L, 30D);
        tradeOrderHandlerEntity.doTrade(buyOrder1);

        Assert.assertEquals(0, tradeVoucherValObjList.size());

        // 600 股价格 29 卖出，成交 600，剩余 400 价格 30 买入挂单
        EngineOrderValObj sellOrder1 = createEngineOrderValObj(OrderSideEnum.SELL,
            0L, symbolId, 600L, 29D);
        tradeOrderHandlerEntity.doTrade(sellOrder1);

        Assert.assertEquals(1, tradeVoucherValObjList.size());
        TradeVoucherValObj tradeVoucherValObj1 = tradeVoucherValObjList.get(0);
        Assert.assertEquals(buyOrder1.getSymbolId(), tradeVoucherValObj1.getSymbolId());
        Assert.assertEquals(buyOrder1.getId(), tradeVoucherValObj1.getMakerId());
        Assert.assertEquals(sellOrder1.getId(), tradeVoucherValObj1.getTakeId());
        Assert.assertEquals(sellOrder1.getSide(), tradeVoucherValObj1.getTakerSide());
        Assert.assertEquals(sellOrder1.getQuantity(), tradeVoucherValObj1.getQuantity());
        Assert.assertEquals(buyOrder1.getPrice(), tradeVoucherValObj1.getPrice());

        // 500 股价格 28 卖出，成交 400，剩余 100 价格 28 卖出挂单
        EngineOrderValObj sellOrder2 = createEngineOrderValObj(OrderSideEnum.SELL,
            0L, symbolId, 500L, 28D);
        tradeOrderHandlerEntity.doTrade(sellOrder2);

        Assert.assertEquals(2, tradeVoucherValObjList.size());
        TradeVoucherValObj tradeVoucherValObj2 = tradeVoucherValObjList.get(1);
        Assert.assertEquals(buyOrder1.getSymbolId(), tradeVoucherValObj2.getSymbolId());
        Assert.assertEquals(buyOrder1.getId(), tradeVoucherValObj2.getMakerId());
        Assert.assertEquals(sellOrder2.getId(), tradeVoucherValObj2.getTakeId());
        Assert.assertEquals(sellOrder2.getSide(), tradeVoucherValObj2.getTakerSide());
        Assert.assertEquals(400L, tradeVoucherValObj2.getQuantity().longValue());
        Assert.assertEquals(buyOrder1.getPrice(), tradeVoucherValObj2.getPrice());

        // 800 股价格 32 买入，成交 100，剩余 700 价格 32 买入挂单
        EngineOrderValObj buyOrder2 = createEngineOrderValObj(OrderSideEnum.BUY,
            0L, symbolId, 800L, 32D);
        tradeOrderHandlerEntity.doTrade(buyOrder2);

        Assert.assertEquals(3, tradeVoucherValObjList.size());
        TradeVoucherValObj tradeVoucherValObj3 = tradeVoucherValObjList.get(2);
        Assert.assertEquals(sellOrder2.getSymbolId(), tradeVoucherValObj3.getSymbolId());
        Assert.assertEquals(sellOrder2.getId(), tradeVoucherValObj3.getMakerId());
        Assert.assertEquals(buyOrder2.getId(), tradeVoucherValObj3.getTakeId());
        Assert.assertEquals(buyOrder2.getSide(), tradeVoucherValObj3.getTakerSide());
        Assert.assertEquals(100L, tradeVoucherValObj3.getQuantity().longValue());
        Assert.assertEquals(sellOrder2.getPrice(), tradeVoucherValObj3.getPrice());

        // 撤销最后一个买入委托单
        tradeOrderHandlerEntity.doCancel(buyOrder2.getId());

        Assert.assertEquals(1, cancelVoucherValObjList.size());
        CancelVoucherValObj cancelVoucherValObj = cancelVoucherValObjList.get(0);
        Assert.assertEquals(buyOrder2.getId(), cancelVoucherValObj.getOrderId());
        Assert.assertEquals(700L, cancelVoucherValObj.getRemainQuantity().longValue());
    }

}
