package com.yuanzhixiang.trade.engine.trade.domain.model.entity.market;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialMarketKLinePO;

import cn.hutool.core.date.DateUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;
import com.yuanzhixiang.trade.engine.trade.domain.repository.MarketKLineRepository;

/**
 * 抽象 k 线行情生成逻辑
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 20:26:09
 */
public abstract class AbstractMarketKLineEntity {

    @Autowired
    private MarketKLineRepository marketKLineRepository;

    /**
     * 设置 k 线级别
     *
     * @return k 线级别
     */
    protected abstract MarketKLineLevel marketKLineLevel();

    /**
     * 判断成交单的时间是否是在该 k 线覆盖的时间范围内
     *
     * @param kLineTime   k 线时间
     * @param voucherTime 成交单时间
     * @return 是否在覆盖范围内
     */
    private boolean timeIsCover(LocalDateTime kLineTime, LocalDateTime voucherTime) {
        // 两时间秒钟的时间戳相减等于 k 线时间级别 -1 则表示是同一个时间
        // 比如 1 秒钟线则是相减为 0 说明在同一秒
        return Math.abs(DateUtil.toInstant(kLineTime).toEpochMilli() / 1000 -
                DateUtil.toInstant(voucherTime).toEpochMilli() / 1000) < marketKLineLevel().getLength();
    }

    /**
     * 修改 k 线
     *
     * @param tradeVoucherValObj 成交单
     */
    public void postKLine(TradeVoucherValObj tradeVoucherValObj) {
        Optional<FinancialMarketKLinePO> marketKLine = marketKLineRepository.getMarketKLine(tradeVoucherValObj.getSymbolId(), marketKLineLevel());
        if (marketKLine.isPresent() && timeIsCover(marketKLine.get().getTime(), tradeVoucherValObj.getTradeTime())) {
            // 修改该 k 线的相关价格
            FinancialMarketKLinePO financialMarketKLinePO = marketKLine.get();
            // 修改收盘价
            financialMarketKLinePO.setClose(tradeVoucherValObj.getPrice());
            // 修改最高价
            if (tradeVoucherValObj.getPrice() > financialMarketKLinePO.getHigh()) {
                financialMarketKLinePO.setHigh(tradeVoucherValObj.getPrice());
            }
            // 修改最低价
            if (tradeVoucherValObj.getPrice() < financialMarketKLinePO.getHigh()) {
                financialMarketKLinePO.setLow(tradeVoucherValObj.getPrice());
            }

            // 累加成交量
            financialMarketKLinePO.setVolume(financialMarketKLinePO.getVolume() + tradeVoucherValObj.getQuantity());
            marketKLineRepository.updateById(financialMarketKLinePO);
        } else {
            // 创建 k 线
            FinancialMarketKLinePO financialMarketKLinePO = new FinancialMarketKLinePO();
            financialMarketKLinePO.setSymbolId(tradeVoucherValObj.getSymbolId());
            financialMarketKLinePO.setLevel(marketKLineLevel().name());

            financialMarketKLinePO.setOpen(tradeVoucherValObj.getPrice());
            financialMarketKLinePO.setClose(tradeVoucherValObj.getPrice());
            financialMarketKLinePO.setHigh(tradeVoucherValObj.getPrice());
            financialMarketKLinePO.setLow(tradeVoucherValObj.getPrice());

            financialMarketKLinePO.setVolume(tradeVoucherValObj.getQuantity());

            // 以当前时间作为开始时间
            financialMarketKLinePO.setTime(tradeVoucherValObj.getTradeTime());

            marketKLineRepository.postMarketKLine(financialMarketKLinePO);
        }
    }
}
