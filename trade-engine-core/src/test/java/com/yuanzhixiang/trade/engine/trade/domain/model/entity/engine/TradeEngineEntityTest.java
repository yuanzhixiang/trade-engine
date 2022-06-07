package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeOrderEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

/**
 * 交易引擎实体单元测试
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 08:41
 */
public class TradeEngineEntityTest extends BaseApplication {

    @Autowired
    private TradeOrderEntityFactory tradeOrderEntityFactory;

    /**
     * 引擎开启测试，不报错就是正常
     */
    @Test
    public void _0001() {
        TradeEngineEntity tradeEngineEntity = new TradeEngineEntity(TradeSymbolUtil.generateSymbolId());
    }

    /**
     * 引擎关闭测试，不报错就是正常
     */
    @Test
    public void _0002() {
        TradeEngineEntity tradeEngineEntity = new TradeEngineEntity(TradeSymbolUtil.generateSymbolId());

        tradeEngineEntity.close();
    }

    /**
     * 向撮合引擎中放入委托单单
     */
    @Test
    public void _0003() {
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        TradeEngineEntity tradeEngineEntity = new TradeEngineEntity(symbolId);

        TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
        tradeOrderValObj.setAccountTradeId(0L);
        tradeOrderValObj.setSymbolId(symbolId);
        tradeOrderValObj.setQuantity(10000L);
        tradeOrderValObj.setPrice(10D);
        tradeOrderValObj.setSide(OrderSideEnum.BUY.name());
        tradeOrderValObj.setType(OrderTypeEnum.LIMIT.name());

        // 存储委托单
        TradeOrderEntity tradeOrderEntity = tradeOrderEntityFactory.postTradeOrder(tradeOrderValObj);

        tradeEngineEntity.submitTradeOrder(tradeOrderEntity);
    }

}
