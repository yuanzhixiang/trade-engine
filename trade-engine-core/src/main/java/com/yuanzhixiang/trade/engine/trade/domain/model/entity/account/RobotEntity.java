package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.yuanzhixiang.trade.engine.trade.application.MarketEntityApplication;
import com.yuanzhixiang.trade.engine.trade.application.UserAggregateApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.BaseEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.user.UserFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountTradeValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineValObj;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;
import com.yuanzhixiang.trade.engine.trade.utils.MathUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.request.KMarketGetRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.AccountSymbolVO;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.CanCancelOrderVO;
import lombok.extern.slf4j.Slf4j;

/**
 * 机器人实体
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/25 14:06
 */
@Slf4j
public class RobotEntity extends BaseEntity {

    private UserAggregateApplication userAggregateApplication;

    private UserFactory userFactory;

    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    private MarketEntityApplication marketEntityApplication;

    /**
     * 该机器人对应的用户聚合根
     */
    private UserAggregate userAggregate;

    public RobotEntity(Long id, Long symbolId) {
        super(id);
        if (symbolId == null) {
            ExceptionHelper.throwSystemException("机器人 symbolId 不能为 null，id: [{}]", id);
        }
        this.symbolId = symbolId;
        userFactory = ApplicationContextHelper.getBean(UserFactory.class);
        userAggregateApplication = ApplicationContextHelper.getBean(UserAggregateApplication.class);
        tradeSymbolAggregateFactory = ApplicationContextHelper.getBean(TradeSymbolAggregateFactory.class);
        marketEntityApplication = ApplicationContextHelper.getBean(MarketEntityApplication.class);
        userAggregate = userFactory.getUser(id);
    }

    /**
     * 该机器人交易的标的
     */
    private Long symbolId;

    /**
     * 执行交易任务
     */
    public void tradeTask() {
        // 按条件撤销委托单
        List<CanCancelOrderVO> canCancelOrderList = userAggregateApplication.getCanCancelOrderList(getId());
        canCancelOrderList.stream()
            // 如果委托单三秒内未成交则撤销委托单
            .filter(canCancelOrderVO -> {
                Duration between = LocalDateTimeUtil
                    .between(canCancelOrderVO.getGmtCreate(), LocalDateTime.now());
                return between.getSeconds() > 3;
            })
            // 撤销委托单
            .forEach(canCancelOrderVO -> {
                tradeSymbolAggregateFactory.getTradeSymbol(symbolId).ifPresent(tradeSymbolAggregate -> {
                    tradeSymbolAggregate.submitCancelOrder(canCancelOrderVO.getId());
                });
            });

        // 发起交易
        PageResult<AccountSymbolVO> accountSymbolPage = userAggregateApplication.getAccountSymbolPage(getId());
        // 没有仓位则只能买
        if (accountSymbolPage.getRecords().size() == 0 && accountSymbolPage.getRecords().get(0).getQuantity() <= 0) {
            doBuy();
        }
        // 有仓位则随机买卖
        else {
            doRandomTrade(accountSymbolPage.getRecords().get(0));
        }
    }

    private void doRandomTrade(AccountSymbolVO accountSymbolVO) {
        // 随机买入或者卖出
        OrderSideEnum orderSide = randomSide();

        // 随机出买入则直接走买入方法
        if (OrderSideEnum.isBuyOrder(orderSide.name())) {
            doBuy();
        } else {
            doSell(accountSymbolVO);
        }
    }

    private void doSell(AccountSymbolVO accountSymbolVO) {
        // 随机出要卖出的数量
        int tradeAmount = MathUtil.randomInt(accountSymbolVO.getQuantity().intValue(), 1000);

        // 随机出要卖出的价格
        Double latestPrice = getSymbolLastPrice();
        Double tradePrice = randomPrice(OrderSideEnum.SELL, latestPrice);

        // 发出卖出委托单
        tradeSymbolAggregateFactory.getTradeSymbol(symbolId).ifPresent(tradeSymbolAggregate -> {
            AccountTradeEntity accountTradeEntity = userAggregate.getAccountTradeEntity();

            TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
            tradeOrderValObj.setAccountTradeId(accountTradeEntity.getId());
            tradeOrderValObj.setSymbolId(symbolId);
            tradeOrderValObj.setQuantity((long) tradeAmount);
            tradeOrderValObj.setPrice(tradePrice);
            tradeOrderValObj.setSide(OrderSideEnum.SELL.name());
            tradeOrderValObj.setType(OrderTypeEnum.LIMIT.name());
            tradeOrderValObj.setCompletedQuantity(0L);
            tradeOrderValObj.setCanceledQuantity(0L);

            tradeSymbolAggregate.submitTradeOrder(tradeOrderValObj);
        });
    }


    private void doBuy() {
        // 查询账户余额
        AccountTradeEntity accountTradeEntity = userAggregate.getAccountTradeEntity();

        // 确定购买价格
        Double latestPrice = getSymbolLastPrice();
        double tradePrice = randomPrice(OrderSideEnum.BUY, latestPrice);

        // 随机出要买入的数量
        int tradeQuantity = randomNumberMultiplesOfHundred();

        // 如果买入要花的钱比余额多的话则重新随机买入数量
        // 如果随机三次还是没钱买就不买了
        int count = 0;
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        while (tradeQuantity * tradePrice > accountTradeValObj.getBalance()) {
            tradeQuantity = randomNumberMultiplesOfHundred();
            count++;

            if (count >= 3) {
                log.info("机器人 [{}] 没有钱进行买入了", getId());
                return;
            }
        }

        final int finalTradeQuantity = tradeQuantity;

        // 发出买入委托单
        tradeSymbolAggregateFactory.getTradeSymbol(symbolId).ifPresent(tradeSymbolAggregate -> {
            TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
            tradeOrderValObj.setAccountTradeId(accountTradeEntity.getId());
            tradeOrderValObj.setSymbolId(symbolId);
            tradeOrderValObj.setQuantity((long) finalTradeQuantity);
            tradeOrderValObj.setPrice(tradePrice);
            tradeOrderValObj.setSide(OrderSideEnum.BUY.name());
            tradeOrderValObj.setType(OrderTypeEnum.LIMIT.name());
            tradeOrderValObj.setCompletedQuantity(0L);
            tradeOrderValObj.setCanceledQuantity(0L);

            tradeSymbolAggregate.submitTradeOrder(tradeOrderValObj);
        });
    }

    private Double getSymbolLastPrice() {
        KMarketGetRequest kMarketGetRequest = new KMarketGetRequest();
        kMarketGetRequest.setSymbolId(symbolId);
        kMarketGetRequest.setLastMarket(Boolean.TRUE);
        List<MarketKLineValObj> marketKLineList = marketEntityApplication.getMarketKLineList(kMarketGetRequest);
        Double latestPrice = 10D;
        if (marketKLineList.size() != 0) {
            latestPrice = marketKLineList.get(0).getClose();
        }
        return latestPrice;
    }

    private OrderSideEnum randomSide() {
        // 随机 50% 的概率买入
        if (MathUtil.randomBoolean()) {
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
        return MathUtil.randomNumberMultiplesOfHundred(1000);
    }
}

enum OrderSideEnum {
    /**
     * 买
     */
    BUY,
    /**
     * 卖
     */
    SELL;

    /**
     * 是买入订单
     *
     * @param side 方向
     * @return 是否是买入订单
     */
    public static boolean isBuyOrder(String side) {
        return BUY.name().equals(side);
    }

    public static boolean isSellOrder(String side) {
        return SELL.name().equals(side);
    }
}
