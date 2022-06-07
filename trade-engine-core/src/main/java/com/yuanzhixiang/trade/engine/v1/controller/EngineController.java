package com.yuanzhixiang.trade.engine.v1.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.TradeEngineEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.Response;

import com.yuanzhixiang.trade.engine.v1.model.vo.TradeOrderVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 委托单服务
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/09 18:47
 */
@Slf4j
public class EngineController {

    private Map<String, TradeEngineEntity> symbolToEngineMap = new HashMap<>();

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 处理委托单
     */
    @PostMapping("/handleOrder")
    public Response<String> handleOrder(@RequestBody TradeOrderVO tradeOrderVO) {
        String symbol = tradeOrderVO.getSymbol();
        TradeEngineEntity tradeEngineEntity = symbolToEngineMap.get(symbol);
        if (tradeEngineEntity == null) {
            log.info("该标的引擎未开启，symbol：[{}]", symbol);
            return Response.success(StrUtil.format("该标的引擎未开启，symbol：[{}]", symbol));
        }

        TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
        BeanUtil.copyProperties(tradeOrderVO, tradeOrderValObj);
//        tradeEngineEntity.handleOrder(tradeOrderValObj);
        return Response.success("处理成功");
    }

    /**
     * 开启竞价引擎
     *
     * @param symbol 交易标的
     */
    @PostMapping("/openMatching")
    public synchronized Response<String> openMatching(
        @RequestParam("symbol") String symbol
    ) {
        TradeEngineEntity tradeEngineEntity = symbolToEngineMap.get(symbol);
        if (tradeEngineEntity == null) {
            TradeSymbolAggregate tradeSymbol = tradeSymbolAggregateFactory.getTradeSymbol(symbol).orElse(null);
            TradeEngineEntity newEngine = new TradeEngineEntity(tradeSymbol.getId());
            symbolToEngineMap.put(symbol, newEngine);
            log.info("开启引擎成功，symbol：[{}]", symbol);
            return Response.success(StrUtil.format("开启引擎成功，symbol：[{}]", symbol));
        }

        log.info("引擎已开启，symbol：[{}]", symbol);
        return Response.success(StrUtil.format("引擎已开启，symbol: [{}]", symbol));
    }

    /**
     * 关闭竞价引擎
     *
     * @param symbol 交易标的
     */
    @PostMapping("/closeMatching")
    public synchronized Response<String> closeMatching(
        @RequestParam("symbol") String symbol
    ) {
        TradeEngineEntity tradeEngineEntity = symbolToEngineMap.get(symbol);
        if (tradeEngineEntity == null) {
            log.info("该标的未启动引擎，symbol：[{}]", symbol);
            return Response.error(StrUtil.format("该标的未启动引擎，symbol: [{}]", symbol));
        }
        symbolToEngineMap.remove(symbol);

        tradeEngineEntity.close();
        log.info("关闭引擎成功，symbol：[{}]", symbol);
        return Response.success(StrUtil.format("关闭引擎成功，symbol: [{}]", symbol));
    }
}
