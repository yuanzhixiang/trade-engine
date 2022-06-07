package com.yuanzhixiang.trade.engine.v1.service.ai;

import org.springframework.stereotype.Service;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 13:31:46
 */
@Service
public class AIService {

    private TradeRobotGroup tradeRobotGroup;

    /**
     * AI 开始交易指定标的
     *
     * @param symbol 标的编码
     */
    public void startTradeSymbol(String symbol) {
        tradeRobotGroup.startTradeSymbol(symbol);
    }

    /**
     * AI 停止交易指定标的
     *
     * @param symbol 标的编码
     */
    public void stopTradeSymbol(String symbol) {
        tradeRobotGroup.stopTradeSymbol(symbol);
    }

    /**
     * 开启一个机器人去做交易
     *
     * @param symbol       标的编码
     * @param holdPosition 持仓数量
     */
    public void startTradeRobot(String symbol, Long holdPosition) {
        tradeRobotGroup.startTradeRobot(symbol, holdPosition);
    }

}
