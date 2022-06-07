package com.yuanzhixiang.trade.engine.v1.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.v1.model.po.TradeVoucherPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.KMarketVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.PriceVO;

import cn.hutool.core.date.DateUtil;
import com.yuanzhixiang.trade.engine.v1.dao.TradeVoucherMapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/17 13:43:04
 */
@Service
public class MarketService1 {

    @Autowired
    private TradeVoucherMapper tradeVoucherMapper;

    public List<PriceVO> selectMarket(String symbol) {
        LambdaQueryWrapper<TradeVoucherPO> priceWrapper = new LambdaQueryWrapper<>();
        priceWrapper.eq(TradeVoucherPO::getSymbol, symbol);
        priceWrapper.orderByAsc(TradeVoucherPO::getTradeTime);
        List<TradeVoucherPO> tradeVoucherPOList = tradeVoucherMapper.selectList(priceWrapper);

        // 按分钟钟收集
        ArrayList<PriceVO> priceList = new ArrayList<>();
        TradeVoucherPO startIndex = null;
        TradeVoucherPO currentIndex = null;

        Long volume = 0L;
        for (TradeVoucherPO tradeVoucherPO : tradeVoucherPOList) {
            // 收集交易量
            volume += tradeVoucherPO.getAmount();

            if (startIndex == null) {
                startIndex = tradeVoucherPO;
                currentIndex = tradeVoucherPO;
                continue;
            }

            // 当分钟数相等时则跳过该数据
            if (tradeVoucherPO.getTradeTime().getMinute() ==
                startIndex.getTradeTime().getMinute()) {
                currentIndex = tradeVoucherPO;
                continue;
            }

            PriceVO priceVO = new PriceVO();
            priceVO.setPrice(
                BigDecimal.valueOf(currentIndex.getPrice()).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue());
            priceVO.setTime(startIndex.getTradeTime());
            priceVO.setVolume(volume);

            priceList.add(priceVO);

            // 清空量能
            volume = 0L;

            startIndex = tradeVoucherPO;
            currentIndex = tradeVoucherPO;
        }

        // 最后一分钟需要在外边进行收集
        if (startIndex != null) {
            PriceVO priceVO = new PriceVO();
            priceVO.setPrice(
                BigDecimal.valueOf(currentIndex.getPrice()).setScale(2, RoundingMode.HALF_UP)
                    .doubleValue());
            priceVO.setTime(startIndex.getTradeTime());
            priceVO.setVolume(volume);

            volume = 0L;

            priceList.add(priceVO);
        }

        return priceList;
    }

    public List<KMarketVO> selectKMarket(String symbol) {
        LambdaQueryWrapper<TradeVoucherPO> priceWrapper = new LambdaQueryWrapper<>();
        priceWrapper.eq(TradeVoucherPO::getSymbol, symbol);
        priceWrapper.orderByAsc(TradeVoucherPO::getTradeTime);
        List<TradeVoucherPO> tradeVoucherPOList = tradeVoucherMapper.selectList(priceWrapper);

        int interval = 5;
        Double open = null;
        Double high = null;
        Double low = null;
        Double close = null;

        Long volume = 0L;

        TradeVoucherPO index = null;

        List<KMarketVO> marketList = new ArrayList<>();

        // 按指定时间收集
        for (TradeVoucherPO tradeVoucherPO : tradeVoucherPOList) {
            // 保存量能
            volume += tradeVoucherPO.getAmount();

            if (open == null) {
                // 开始的第一秒初始化数据
                open = tradeVoucherPO.getPrice();
                high = tradeVoucherPO.getPrice();
                low = tradeVoucherPO.getPrice();
                close = tradeVoucherPO.getPrice();

                index = tradeVoucherPO;
                continue;
            } else {
                // 保存最高价
                if (high < tradeVoucherPO.getPrice()) {
                    high = tradeVoucherPO.getPrice();
                }

                // 保存最低价
                if (low > tradeVoucherPO.getPrice()) {
                    low = tradeVoucherPO.getPrice();
                }
            }

            // 在指定的时间段内则进入 if
            if (DateUtil.toInstant(tradeVoucherPO.getTradeTime()).toEpochMilli() / 1000 -
                DateUtil.toInstant(index.getTradeTime()).toEpochMilli() / 1000 <= interval) {
                continue;
            }

            // 保存收盘价
            close = tradeVoucherPO.getPrice();


            KMarketVO kMarketVO = new KMarketVO();
            kMarketVO.setTime(tradeVoucherPO.getTradeTime());
            kMarketVO.setOpen(open);
            kMarketVO.setClose(close);
            kMarketVO.setLow(low);
            kMarketVO.setHigh(high);
            kMarketVO.setVolume(volume);

            marketList.add(kMarketVO);

            // 清空量能
            volume = 0L;

            open = null;
            low = null;
            high = null;
            close = null;

            index = tradeVoucherPO;
        }

        if (index != null) {
            KMarketVO kMarketVO = new KMarketVO();
            kMarketVO.setTime(index.getTradeTime());
            kMarketVO.setOpen(open);
            kMarketVO.setClose(close);
            kMarketVO.setLow(low);
            kMarketVO.setHigh(high);
            kMarketVO.setVolume(volume);

            marketList.add(kMarketVO);
        }

        return marketList;
    }
}
