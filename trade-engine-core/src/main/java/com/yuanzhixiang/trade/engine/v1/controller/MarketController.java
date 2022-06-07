package com.yuanzhixiang.trade.engine.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.model.vo.KMarketVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.PriceVO;
import com.yuanzhixiang.trade.engine.v1.service.MarketService1;
import lombok.extern.slf4j.Slf4j;

/**
 * 行情服务
 *
 * @author zhixiang.yuan
 * @since 2021/02/17 13:41:04
 */
@Slf4j
public class MarketController {

    @Autowired
    private MarketService1 marketService1;

    @GetMapping("selectMarket")
    public Response<List<PriceVO>> selectMarket(@RequestParam("symbol") String symbol) {
        return Response.success(marketService1.selectMarket(symbol));
    }

    @GetMapping("selectKMarket")
    public Response<List<KMarketVO>> selectKMarket(@RequestParam("symbol") String symbol) {
        return Response.success(marketService1.selectKMarket(symbol));
    }
}
