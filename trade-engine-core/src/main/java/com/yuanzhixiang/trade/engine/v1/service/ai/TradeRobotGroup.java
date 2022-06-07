package com.yuanzhixiang.trade.engine.v1.service.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.yuanzhixiang.trade.engine.v1.controller.HoldPositionController;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;
import com.yuanzhixiang.trade.engine.v1.service.UserService;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 17:53:10
 */
public class TradeRobotGroup implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private HoldPositionController holdPositionController;

    private List<TradeRobot> tradeRobotList = new ArrayList<>();

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
        new ScheduledThreadPoolExecutor(100);

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        // 初始化十只机器人
//        for (int i = 0; i < 10; i++) {
//            TradeRobot tradeRobot = TradeRobot.builder()
//                .userId(userService.initUser())
//                .build();
//
//            scheduledThreadPoolExecutor
//                .scheduleWithFixedDelay(tradeRobot, 1, 1, TimeUnit.SECONDS);
//            tradeRobotList.add(tradeRobot);
//        }
    }

    public void startTradeRobot(String symbol, Long holdPosition) {
        // 初始化机器人
        TradeRobot tradeRobot = TradeRobot.builder()
                .userId(userService.initUser())
                .build();

        tradeRobot.startTradeSymbol(symbol);

        // 初始化用户持仓
        HoldPositionVO holdPositionVO = new HoldPositionVO();
        holdPositionVO.setUserId(tradeRobot.getUserId());
        holdPositionVO.setSymbol(symbol);
        holdPositionVO.setPositionAmount(holdPosition);
        holdPositionVO.setCostPrice(1D);

        holdPositionController.initUserHoldPosition(holdPositionVO);

        // 开始交易
        scheduledThreadPoolExecutor
                .scheduleWithFixedDelay(tradeRobot, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 开始交易指定标的
     *
     * @param symbol 标的编码
     */
    public void startTradeSymbol(String symbol) {
        for (TradeRobot tradeRobot : tradeRobotList) {
            tradeRobot.startTradeSymbol(symbol);
        }
    }

    /**
     * 停止交易指定标的
     *
     * @param symbol 标的编码
     */
    public void stopTradeSymbol(String symbol) {
        for (TradeRobot tradeRobot : tradeRobotList) {
            tradeRobot.stopTradeSymbol(symbol);
        }
    }
}

