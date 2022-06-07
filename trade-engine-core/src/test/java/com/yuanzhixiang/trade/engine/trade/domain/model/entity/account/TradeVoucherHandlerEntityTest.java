package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.AccountTradeEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;

/**
 * 交易凭证处理器实体测试类
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 20:27
 */
public class TradeVoucherHandlerEntityTest extends BaseApplication {

    @Autowired
    private TradeVoucherHandlerEntity tradeVoucherHandlerEntity;

    @Autowired
    private AccountTradeEntityFactory accountTradeEntityFactory;

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 检查交易凭证处理器处理结果是否正确，思路是在处理过后将所有账户的金额和标的数量加起来看是否和原来相同，如果相同则说明没有出错
     */
    @Test
    public void _0001() {
        // 创建交易标的
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(TradeSymbolUtil.generateSymbol());
        TradeSymbolAggregate tradeSymbolAggregate = tradeSymbolAggregateFactory
            .postTradeSymbol(tradeSymbolValObj);


        // todo 创建单个交易账户
        // todo 创建多个交易账户
        AccountTradeEntity account1 = accountTradeEntityFactory.postAccountTrade();

        TradeVoucherValObj tradeVoucherValObj = new TradeVoucherValObj();
        tradeVoucherValObj.setMakerId(0L);
        tradeVoucherValObj.setTakeId(0L);
        tradeVoucherValObj.setTakerSide("");
        tradeVoucherValObj.setQuantity(0L);
        tradeVoucherValObj.setPrice(0.0D);
        tradeVoucherValObj.setSymbolId(0L);
        tradeVoucherValObj.setTradeTime(LocalDateTime.now());

        // todo 向交易标的中放入委托单

        // todo 向交易凭证处理器中放入交易凭证

        // todo 测试是否起到了该有的转账效果

    }
}
