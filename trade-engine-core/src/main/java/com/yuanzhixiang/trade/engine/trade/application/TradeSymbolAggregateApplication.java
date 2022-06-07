package com.yuanzhixiang.trade.engine.trade.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.application.converter.TradeSymbolApplicationConverter;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.repository.TradeSymbolRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeSymbolParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialSymbolPO;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeSymbolGetPageRequest;

/**
 * 交易标的聚合根应用层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 11:47
 */
@Component
public class TradeSymbolAggregateApplication {

    @Autowired
    private TradeSymbolRepository tradeSymbolRepository;

    @Autowired
    private TradeSymbolApplicationConverter tradeSymbolApplicationConverter;

    /**
     * 查询交易标的列表
     *
     * @return 交易标的列表
     */
    public List<TradeSymbolAggregate> getTradeSymbolList() {
        return tradeSymbolRepository.getTradeSymbolList().stream()
            .map(financialSymbolPO ->
                new TradeSymbolAggregate(financialSymbolPO.getId(), financialSymbolPO.getSymbol()))
            .collect(Collectors.toList());
    }

    /**
     * 查询交易标的分页列表
     *
     * @param request 查询请求
     * @return 查询到的分页结果
     */
    public PageResult<TradeSymbolAggregate> getTradeSymbolPage(TradeSymbolGetPageRequest request) {
        TradeSymbolParam tradeSymbolParam = tradeSymbolApplicationConverter.symbolGetRequestToParam(request);
        PageResult<FinancialSymbolPO> tradeSymbolPage = tradeSymbolRepository.getTradeSymbolPage(tradeSymbolParam);
        return PageResult.getPageResult(tradeSymbolPage,
            financialSymbolPOS -> financialSymbolPOS.stream().map(financialSymbolPO ->
                new TradeSymbolAggregate(financialSymbolPO.getId(), financialSymbolPO.getSymbol()))
                .collect(Collectors.toList())
        );
    }
}
