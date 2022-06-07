package com.yuanzhixiang.trade.engine.v1.controller;

import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.model.vo.GameVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;
import com.yuanzhixiang.trade.engine.v1.service.SymbolService;
import com.yuanzhixiang.trade.engine.v1.service.ai.AIService;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:09:39
 */
public class GameController {

    @Autowired
    private UserController userController;

    @Autowired
    private EngineController engineController;

    @Autowired
    private AIService aiService;

    @Autowired
    private SymbolService symbolService;

    @Autowired
    private HoldPositionController holdPositionController;

    @Autowired
    private AIController aiController;

    @PostMapping("startGame")
    public Response<GameVO> startGame() {
        GameVO gameVO = new GameVO();

        // 注册用户
        Response<Long> register = userController.register();
        gameVO.setUserId(register.getContent());

        // 开启标的
        Double openPrice = 20D;
        String symbol = UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 6).toUpperCase(Locale.ROOT);
        gameVO.setSymbol(symbol);

        engineController.openMatching(symbol);

        symbolService.initLatestPrice(symbol, openPrice);

        // 初始化用户持仓
        HoldPositionVO holdPositionVO = new HoldPositionVO();
        holdPositionVO.setUserId(gameVO.getUserId());
        holdPositionVO.setSymbol(gameVO.getSymbol());
        holdPositionVO.setPositionAmount(100000L);
        holdPositionVO.setCostPrice(1D);

        holdPositionController.initUserHoldPosition(holdPositionVO);

//        // 开启交易机器人
//        aiService.startTradeSymbol(symbol);

        // 初始化机器人
        aiController.startTradeRobot(symbol, 0L);
        aiController.startTradeRobot(symbol, 0L);
        aiController.startTradeRobot(symbol, 0L);
        aiController.startTradeRobot(symbol, 0L);
        aiController.startTradeRobot(symbol, 0L);

        return Response.success(gameVO);
    }

    @PostMapping("startSelfGame")
    public Response<GameVO> startSelfGame() {
        GameVO gameVO = new GameVO();

        // 注册用户
        Response<Long> register = userController.register();
        gameVO.setUserId(register.getContent());

        // 开启标的
        Double openPrice = 20D;
        String symbol = UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 6).toUpperCase(Locale.ROOT);
        gameVO.setSymbol(symbol);

        engineController.openMatching(symbol);

        symbolService.initLatestPrice(symbol, openPrice);

        // 初始化用户持仓
        HoldPositionVO holdPositionVO = new HoldPositionVO();
        holdPositionVO.setUserId(gameVO.getUserId());
        holdPositionVO.setSymbol(gameVO.getSymbol());
        holdPositionVO.setPositionAmount(10000L);
        holdPositionVO.setCostPrice(1D);

        holdPositionController.initUserHoldPosition(holdPositionVO);

        return Response.success(gameVO);
    }

}
