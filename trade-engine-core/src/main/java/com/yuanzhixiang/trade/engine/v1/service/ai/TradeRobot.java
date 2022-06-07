package com.yuanzhixiang.trade.engine.v1.service.ai;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import com.yuanzhixiang.trade.engine.v1.controller.HoldPositionController;
import com.yuanzhixiang.trade.engine.v1.controller.SymbolController;
import com.yuanzhixiang.trade.engine.v1.controller.WalletController;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.yuanzhixiang.trade.engine.TradeEngineServiceApplication;
import com.yuanzhixiang.trade.engine.v1.controller.OrderController;
import com.yuanzhixiang.trade.engine.v1.model.vo.CanCancelOrderVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderCancelVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderCreateVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.SymbolVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.WalletVO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * 交易机器人
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 14:29:30
 */
@Slf4j
public class TradeRobot implements Runnable {

    @Builder
    public TradeRobot(
        Long userId
    ) {
        this.userId = userId;
        this.orderController =
            TradeEngineServiceApplication.applicationContext.getBean(OrderController.class);
        this.holdPositionController =
            TradeEngineServiceApplication.applicationContext.getBean(HoldPositionController.class);
        this.symbolController =
            TradeEngineServiceApplication.applicationContext.getBean(SymbolController.class);
        this.walletController =
            TradeEngineServiceApplication.applicationContext.getBean(WalletController.class);
    }

    /**
     * 用户 id
     */
    private final Long userId;

    public Long getUserId() {
        return userId;
    }

    private OrderController orderController;

    private HoldPositionController holdPositionController;

    private SymbolController symbolController;

    private WalletController walletController;
    /**
     * 监控标的列表
     */
    private List<String> symbolList = new ArrayList<>();

    private Random random = new Random();

    /**
     * 开始交易指定标的
     *
     * @param symbol 标的编码
     */
    public void startTradeSymbol(String symbol) {
        symbolList.add(symbol);
    }

    /**
     * 停止交易指定标的
     *
     * @param symbol 标的编码
     */
    public void stopTradeSymbol(String symbol) {
        symbolList.remove(symbol);
    }

    @Override
    public void run() {
        try {
            tradeTask();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void tradeTask() {
        // 按条件撤销委托单
        List<CanCancelOrderVO> canCancelOrderVOList =
            orderController.selectCanCancelOrder(userId).getContent();
        canCancelOrderVOList.stream()
            // 如果委托单三秒内未成交则撤销委托单
            .filter(canCancelOrderVO -> {
                Duration between = LocalDateTimeUtil
                    .between(canCancelOrderVO.getCreateTime(), LocalDateTime.now());
                return between.getSeconds() > 3;
            })
            // 撤销委托单
            .forEach(canCancelOrderVO -> {
                OrderCancelVO orderCancelVO = new OrderCancelVO();
                orderCancelVO.setUserId(userId);
                orderCancelVO.setSymbol(canCancelOrderVO.getSymbol());
                orderCancelVO.setCancelOrderId(canCancelOrderVO.getOrderId());
                orderController.cancelOrder(orderCancelVO);
            });

        // 发起交易
        Map<String, HoldPositionVO> symbolToHoldPositionMap = holdPositionController.selectByUserId(userId).getContent()
            .stream().collect(Collectors.toMap(HoldPositionVO::getSymbol, Function.identity()));
        for (String symbol : symbolList) {
            // 只有当没有持仓的时候才随机买卖方向，否则直接买入
            if (
                symbolToHoldPositionMap.containsKey(symbol)
                    && symbolToHoldPositionMap.get(symbol).getPositionAmount() != 0
            ) {
                doRandomTrade(symbolToHoldPositionMap, symbol);
            } else {
                doBuy(symbol);
            }
        }
    }

    private void doRandomTrade(
        Map<String, HoldPositionVO> symbolToHoldPositionMap,
        String symbol
    ) {
        // 2. 买入多少或者卖出多少
        // 3. 以什么价格进行交易

        // 随机买入或者卖出
        OrderSideEnum orderSide = randomSide();

        // 随机出买入则直接走买入方法
        if (OrderSideEnum.isBuyOrder(orderSide.name())) {
            doBuy(symbol);
        } else {
            doSell(symbolToHoldPositionMap, symbol);
        }
    }

    private void doSell(Map<String, HoldPositionVO> symbolToHoldPositionMap, String symbol) {
        // 随机出要卖出的数量
        HoldPositionVO holdPositionVO = symbolToHoldPositionMap.get(symbol);
        int tradeAmount = randomNumber(holdPositionVO.getPositionAmount().intValue(), 1000);

        // 随机出要卖出的价格
        SymbolVO symbolVO = symbolController.selectSymbol(symbol).getContent();
        Double latestPrice = symbolVO.getLatestPrice();
        Double tradePrice = randomPrice(OrderSideEnum.SELL, latestPrice);

        // 发出卖出委托单
        OrderCreateVO orderCreateVO = new OrderCreateVO();
        orderCreateVO.setUserId(userId);
        orderCreateVO.setSymbol(symbol);
        orderCreateVO.setAmount((long) tradeAmount);
        orderCreateVO.setPrice(tradePrice);
        orderCreateVO.setSide(OrderSideEnum.SELL.name());
        orderCreateVO.setType(OrderTypeEnum.LIMIT.name());

        orderController.createOrder(orderCreateVO);
    }

    private void doBuy(String symbol) {
        // 查询账户余额
        WalletVO walletVO = walletController.selectByUserId(userId).getContent();

        // 确定购买价格
        SymbolVO symbolVO = symbolController.selectSymbol(symbol).getContent();
        Double latestPrice = symbolVO.getLatestPrice();
        double tradePrice = randomPrice(OrderSideEnum.BUY, latestPrice);

        // 随机出要买入的数量
        int tradeAmount = randomNumberMultiplesOfHundred();

        // 如果买入要花的钱比余额多的话则重新随机买入数量
        // 如果随机三次还是没钱买就不买了
        int count = 0;
        while (tradeAmount * tradePrice > walletVO.getBalance()) {
            tradeAmount = randomNumberMultiplesOfHundred();
            count++;

            if (count >= 3) {
                log.info("机器人 [{}] 没有钱进行买入了", userId);
                return;
            }
        }

        // 发出买入委托单
        OrderCreateVO orderCreateVO = new OrderCreateVO();
        orderCreateVO.setUserId(userId);
        orderCreateVO.setSymbol(symbol);
        orderCreateVO.setAmount((long) tradeAmount);
        orderCreateVO.setPrice(tradePrice);
        orderCreateVO.setSide(OrderSideEnum.BUY.name());
        orderCreateVO.setType(OrderTypeEnum.LIMIT.name());

        orderController.createOrder(orderCreateVO);
    }

    private OrderSideEnum randomSide() {
        int randomInt = random.nextInt(10);
        // 随机 50% 的概率买入
        if (randomInt < 5) {
            return OrderSideEnum.BUY;
        } else {
            return OrderSideEnum.SELL;
        }
    }

    /**
     * 随机价格
     *
     * @param orderSide 买卖方向
     * @param basePrice 基于该价格进行随机
     */
    private double randomPrice(OrderSideEnum orderSide, double basePrice) {
        if (OrderSideEnum.isBuyOrder(orderSide.name())) {
            return basePrice + basePrice * 0.01;
        } else {
            return basePrice - basePrice * 0.01;
        }
    }

    private int randomNumberMultiplesOfHundred() {
        return randomNumberMultiplesOfHundred(1000);
    }

    /**
     * 随机数量，随机出来的数字是 100 的整数倍
     *
     * @param bound 随机出来的数字不会大于该数字
     * @return 随机出来的数字
     */
    private int randomNumberMultiplesOfHundred(int bound) {
        int number = random.nextInt(bound);
        // 在随机出来的数字上加 100
        return (number / 100 + 1) * 100;
    }

    /**
     * 随机一个数字，但是数字不会是 0
     *
     * @param bound 随机数字的上线
     * @param max   最大不超过这个数字
     * @return 随机出来的数字
     */
    private int randomNumber(int bound, int max) {
        int randomNumber = random.nextInt(bound);
        if (randomNumber > max) {
            return max;
        }
        return randomNumber == 0 ? randomNumber + 1 : randomNumber;
    }
}
