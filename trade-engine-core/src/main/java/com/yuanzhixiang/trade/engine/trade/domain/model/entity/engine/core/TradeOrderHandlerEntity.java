package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import com.lmax.disruptor.EventHandler;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.TradeOrderEvent;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * todo 目前没有处理限价与市价成交
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 09:41:01
 */
@Slf4j
public class TradeOrderHandlerEntity implements EventHandler<TradeOrderEvent> {

    public TradeOrderHandlerEntity(Long symbolId, TradeVoucherSender tradeVoucherSender) {
        this.symbolId = symbolId;
        this.tradeVoucherSender = tradeVoucherSender;
    }

    /**
     * 提交交易凭证
     */
    private final TradeVoucherSender tradeVoucherSender;

    /**
     * 委托账本
     */
    private final TradeOrderBook tradeOrderBook = new TradeOrderBook();

    /**
     * 用于确定订单的顺序，初始时设置为 long 最小值
     */
    private long time = Long.MIN_VALUE;

    private final Long symbolId;

    @Override
    public void onEvent(TradeOrderEvent event, long sequence, boolean endOfBatch) {
        // 处理买卖单
        if (OrderActionEnum.isCreate(event.getOrderAction())) {
            doTrade(event.getEngineOrderValObj());
        }
        // 处理撤单
        else if (OrderActionEnum.isCancel(event.getOrderAction())) {
            doCancel(event.getCancelOrderId());
        }
    }

    /**
     * 处理需要交易的委托单
     *
     * @param engineOrderValObj 委托单
     */
    public void doTrade(EngineOrderValObj engineOrderValObj) {
        // 固定订单创建时间
        engineOrderValObj.setCreateTime(time++);
        // 设置未交易数量
        engineOrderValObj.setRemainQuantity(engineOrderValObj.getQuantity());
        // 委托单定序
        doSequence(engineOrderValObj);
        // 触发匹配
        doMatching();
    }

    /**
     * 取消订单
     *
     * @param cancelOrderId 将要取消的委托单 id
     */
    public void doCancel(Long cancelOrderId) {
        Map<Long, EngineOrderValObj> orderBOMap = tradeOrderBook.getOrderBOMap();
        EngineOrderValObj removeEngineOrderValObj = orderBOMap.remove(cancelOrderId);
        if (removeEngineOrderValObj == null) {
            // 没有该委托单说明单子已经撤销
            return;
        }
        removeEngineOrderValObj.setCancel(true);

        CancelVoucherValObj cancelVoucherValObj = new CancelVoucherValObj();
        cancelVoucherValObj.setOrderId(removeEngineOrderValObj.getId());
        cancelVoucherValObj.setRemainQuantity(removeEngineOrderValObj.getRemainQuantity());
        tradeVoucherSender.submitCancelVoucher(cancelVoucherValObj);
    }

    /**
     * 该委托单定序
     *
     * @param engineOrderValObj 新的委托单
     */
    private void doSequence(EngineOrderValObj engineOrderValObj) {
        // 给交易单定序
        if (OrderSideEnum.isBuyOrder(engineOrderValObj.getSide())) {
            tradeOrderBook.insertBuyOrder(engineOrderValObj);
        } else {
            tradeOrderBook.insertSellOrder(engineOrderValObj);
        }
    }

    /**
     * 匹配订单
     */
    private void doMatching() {
        // 处理委托单
        List<Deque<EngineOrderValObj>> buyOrderList = tradeOrderBook.getBuyOrderList();
        List<Deque<EngineOrderValObj>> sellOrderList = tradeOrderBook.getSellOrderList();

        EngineOrderValObj buy = getOrderBO(buyOrderList);
        EngineOrderValObj sell = getOrderBO(sellOrderList);

        // 如果一边没找到委托单或者买卖价格无法达成成交则将委托单再次放回委托账本
        if (buy == null || sell == null || buy.getPrice() < sell.getPrice()) {
            tradeOrderBook.backInsertBuyOrder(buy);
            tradeOrderBook.backInsertSellOrder(sell);
            return;
        }

        // 定成交价
        double tradePrice;
        if (buy.getCreateTime() > sell.getCreateTime()) {
            // 买单是吃单则用卖单价作为成交价
            tradePrice = sell.getPrice();
        } else {
            // 卖单是吃单则用买单价作为成交价
            tradePrice = buy.getPrice();
        }

        // 计算成交数量（取买单与卖单中的最小值）
        long minQuantity = Math.min(buy.getRemainQuantity(), sell.getRemainQuantity());
        buy.setRemainQuantity(buy.getRemainQuantity() - minQuantity);
        sell.setRemainQuantity(sell.getRemainQuantity() - minQuantity);

        // 未交易完的订单继续放回队列，否则从视图中移除
        // 如果出现了只交易了一部分的情况则需要再次触发匹配交易
        boolean needNextMatch = false;
        if (buy.getRemainQuantity() != 0) {
            tradeOrderBook.backInsertBuyOrder(buy);
            needNextMatch = true;
        } else {
            tradeOrderBook.getOrderBOMap().remove(buy.getId());
        }

        if (sell.getRemainQuantity() != 0) {
            tradeOrderBook.backInsertSellOrder(sell);
            needNextMatch = true;
        } else {
            tradeOrderBook.getOrderBOMap().remove(sell.getId());
        }

        // 生成交易凭证给下游服务
        TradeVoucherValObj tradeVoucherValObj = new TradeVoucherValObj();
        tradeVoucherValObj.setQuantity(minQuantity);
        tradeVoucherValObj.setPrice(tradePrice);
        tradeVoucherValObj.setSymbolId(symbolId);
        tradeVoucherValObj.setTradeTime(LocalDateTime.now());
        if (buy.getCreateTime() > sell.getCreateTime()) {
            tradeVoucherValObj.setMakerId(sell.getId());
            tradeVoucherValObj.setTakeId(buy.getId());
            tradeVoucherValObj.setTakerSide(buy.getSide());
        } else {
            tradeVoucherValObj.setMakerId(buy.getId());
            tradeVoucherValObj.setTakeId(sell.getId());
            tradeVoucherValObj.setTakerSide(sell.getSide());
        }

        // 提交交易凭证
        tradeVoucherSender.submitTradeVoucher(tradeVoucherValObj);

        // 触发再次匹配
        if (needNextMatch) {
            doMatching();
        }
    }

    /**
     * 从价格队列里面找出一条能够交易的委托单
     *
     * @param orderBookList 价格队列
     * @return 能够成交的委托单
     */
    private EngineOrderValObj getOrderBO(List<Deque<EngineOrderValObj>> orderBookList) {
        if (orderBookList.size() == 0) {
            // 委托账本中没有价格队列则直接返回
            return null;
        }

        // 取出队头价格队列
        Deque<EngineOrderValObj> tradeOrderValObjDeque = orderBookList.get(0);

        // 如果队头价格队列为空则直接移除该价格队列
        if (tradeOrderValObjDeque.size() == 0) {
            orderBookList.remove(0);
            // 重新获取价格队列
            return getOrderBO(orderBookList);
        }

        EngineOrderValObj engineOrderValObj = tradeOrderValObjDeque.pollFirst();
        // 找出没被取消的订单
        while (engineOrderValObj.getCancel()) {
            if (tradeOrderValObjDeque.size() == 0) {
                orderBookList.remove(0);
                // 重新获取价格队列
                return getOrderBO(orderBookList);
            }
            engineOrderValObj = tradeOrderValObjDeque.pollFirst();
        }

        // 取出元素之后如果队列元素为空则将该队列移除
        if (tradeOrderValObjDeque.size() == 0) {
            orderBookList.remove(0);
        }

        return engineOrderValObj;
    }
}

