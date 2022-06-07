package com.yuanzhixiang.trade.engine.trade.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.repository.MarketKLineRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.MarketKLineParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialMarketKLinePO;
import com.yuanzhixiang.trade.engine.trade.web.model.request.KMarketGetRequest;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineValObj;

/**
 * 行情实体应用层
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 21:15:06
 */
@Component
public class MarketEntityApplication {

    @Autowired
    private MarketKLineRepository marketKLineRepository;

    public List<MarketKLineValObj> getMarketKLineList(KMarketGetRequest kMarketGetRequest) {
        MarketKLineParam marketKLineParam = new MarketKLineParam();
        marketKLineParam.setLevel(kMarketGetRequest.getLevel());
        marketKLineParam.setSymbolId(kMarketGetRequest.getSymbolId());
        marketKLineParam.setLastMarket(kMarketGetRequest.getLastMarket());
        marketKLineParam.setGeTradeTime(kMarketGetRequest.getGeTradeTime());

        return marketKLineRepository.getMarketKLineList(marketKLineParam)
                .stream().map(this::poToValObj).collect(Collectors.toList());
    }

    /**
     * po 转 valObj
     */
    private MarketKLineValObj poToValObj(FinancialMarketKLinePO financialMarketKLinePO) {
        MarketKLineValObj marketKLineValObj = new MarketKLineValObj();
        marketKLineValObj.setSymbolId(financialMarketKLinePO.getSymbolId());
        marketKLineValObj.setLevel(financialMarketKLinePO.getLevel());
        marketKLineValObj.setTime(financialMarketKLinePO.getTime());
        marketKLineValObj.setOpen(financialMarketKLinePO.getOpen());
        marketKLineValObj.setClose(financialMarketKLinePO.getClose());
        marketKLineValObj.setHigh(financialMarketKLinePO.getHigh());
        marketKLineValObj.setLow(financialMarketKLinePO.getLow());
        marketKLineValObj.setVolume(financialMarketKLinePO.getVolume());
        return marketKLineValObj;
    }
}
