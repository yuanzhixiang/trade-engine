package com.yuanzhixiang.trade.engine.trade.domain.model.factory;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.repository.TradeSymbolRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialSymbolPO;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.TradeSymbolException;

/**
 * 交易标的聚合根服务
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 22:24
 */
@Component
public class TradeSymbolAggregateFactory {

    private final TradeSymbolRepository tradeSymbolRepository;

    public TradeSymbolAggregateFactory(
        TradeSymbolRepository tradeSymbolRepository
    ) {
        this.tradeSymbolRepository = tradeSymbolRepository;
    }

    /**
     * 查询交易标的聚合根
     *
     * @param symbol 标的编码
     * @return 交易标的聚合根
     */
    public Optional<TradeSymbolAggregate> getTradeSymbol(String symbol) {
        FinancialSymbolPO financialSymbolPO = tradeSymbolRepository.getTradeSymbol(symbol);

        if (financialSymbolPO != null) {
            return Optional.of(new TradeSymbolAggregate(financialSymbolPO.getId(), symbol));
        } else {
            return Optional.empty();
        }
    }

    /**
     * 查询交易标的聚合根
     *
     * @param id 聚合根主键
     * @return 交易标的聚合根
     */
    public Optional<TradeSymbolAggregate> getTradeSymbol(Long id) {
        FinancialSymbolPO financialSymbolPO = tradeSymbolRepository.getTradeSymbol(id);
        // 填充交易标的信息
        if (financialSymbolPO != null) {
            return Optional
                .of(new TradeSymbolAggregate(financialSymbolPO.getId(), financialSymbolPO.getSymbol()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * 创建交易标的
     *
     * @param tradeSymbolValObj 交易标的值对象
     */
    public TradeSymbolAggregate postTradeSymbol(TradeSymbolValObj tradeSymbolValObj) {
        TradeSymbolAggregate.checkSymbol(tradeSymbolValObj.getSymbol());
        Optional<TradeSymbolAggregate> tradeSymbol = getTradeSymbol(tradeSymbolValObj.getSymbol());

        if (tradeSymbol.isPresent()) {
            ExceptionHelper.throwTradeSymbolException("该交易标的已存在，不允许重复创建，symbol: [{}]", tradeSymbolValObj.getSymbol());
        }

        Long id = tradeSymbolRepository.postTradeSymbol(tradeSymbolValObj);

        return getTradeSymbol(id).orElseThrow(
            () -> ExceptionHelper.createException(TradeSymbolException.class, "查询交易标的失败，id: [{}], symbol: [{}]", id,
                tradeSymbolValObj.getSymbol()));
    }
}
