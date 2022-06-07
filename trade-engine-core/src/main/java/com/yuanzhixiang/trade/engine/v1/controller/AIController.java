package com.yuanzhixiang.trade.engine.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.service.ai.AIService;

/**
 * ai 服务
 *
 * @author zhixiang.yuan
 * @since 2021/02/19 22:20:53
 */
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 开启机器人来做交易
     *
     * @param symbol       交易标的
     * @param holdPosition 持仓数量
     */
    @PostMapping("/startTradeRobot")
    public Response<Void> startTradeRobot(
        @RequestParam("symbol") String symbol,
        @RequestParam("holdPosition") Long holdPosition
    ) {
        aiService.startTradeRobot(symbol, holdPosition);
        return Response.success();
    }
}
