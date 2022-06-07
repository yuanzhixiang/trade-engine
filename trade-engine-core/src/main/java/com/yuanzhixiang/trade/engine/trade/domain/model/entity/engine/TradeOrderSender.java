package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine;

import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.TradeOrderHandlerEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.TradeVoucherSenderDisruptor;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineOrderValObj;

/**
 * 委托单发送器
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 09:40:44
 */
public class TradeOrderSender {

    /**
     * 委托单队列
     */
    private final Disruptor<TradeOrderEvent> disruptor;

    private final RingBuffer<TradeOrderEvent> ringBuffer;

    /**
     * 交易凭证发送器
     */
    private final TradeVoucherSenderDisruptor tradeVoucherSenderDisruptor;

    public TradeOrderSender(Long symbolId) {
        disruptor = new Disruptor<>(
                TradeOrderEvent::new, 1024 * 1024, Executors.defaultThreadFactory(),
                ProducerType.MULTI, new BlockingWaitStrategy()
        );

        // 初始化交易凭证发射器
        tradeVoucherSenderDisruptor = new TradeVoucherSenderDisruptor();

        // 放入委托单处理器
        disruptor
            .handleEventsWith(new TradeOrderHandlerEntity(symbolId, tradeVoucherSenderDisruptor))
            .then((event, sequence, endOfBatch) -> event.clear());

        ringBuffer = disruptor.getRingBuffer();

        disruptor.start();
    }

    /**
     * 关闭发送器
     */
    public void closeSender() {
        disruptor.shutdown();

        // 关闭交易凭证发送器
        tradeVoucherSenderDisruptor.closeSender();
    }

    public void submitTradeOrder(TradeOrderEntity entity) {
        // 去除环形队列下一个放入的序号
        long sequence = ringBuffer.next();
        try {
            // 从环形队列中取出该序号指向的容器
            TradeOrderEvent event = ringBuffer.get(sequence);
            // 将新委托单实体放入容器
            event.setCancelOrderId(null);
            event.setOrderAction(OrderActionEnum.CREATE);
            event.setEngineOrderValObj(buildEngineOrderValObj(entity));
        } finally {
            // 发送委托单
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 构建送入引擎的委托单
     *
     * @param entity 委托单实体
     * @return 将被送入引擎的委托单
     */
    public static EngineOrderValObj buildEngineOrderValObj(TradeOrderEntity entity) {
        EngineOrderValObj engineOrderValObj = new EngineOrderValObj();
        engineOrderValObj.setId(entity.getId());

        TradeOrderValObj tradeOrderValObj = entity.getTradeOrderValObj();

        engineOrderValObj.setAccountTradeId(tradeOrderValObj.getAccountTradeId());
        engineOrderValObj.setSymbolId(tradeOrderValObj.getSymbolId());
        // 减掉完成的数量才是需要交易的数量
        engineOrderValObj.setQuantity(tradeOrderValObj.getQuantity() - tradeOrderValObj.getCompletedQuantity());
        engineOrderValObj.setPrice(tradeOrderValObj.getPrice());
        engineOrderValObj.setSide(tradeOrderValObj.getSide());
        engineOrderValObj.setType(tradeOrderValObj.getType());

        return engineOrderValObj;
    }

    /**
     * 提交需要撤销的委托单
     *
     * @param cancelOrderId 委托单 id
     */
    public void submitCancelOrder(Long cancelOrderId) {
        // 去除环形队列下一个放入的序号
        long sequence = ringBuffer.next();
        try {
            // 从环形队列中取出该序号指向的容器
            TradeOrderEvent event = ringBuffer.get(sequence);
            // 将新委托单实体放入容器
            event.setEngineOrderValObj(null);
            event.setOrderAction(OrderActionEnum.CANCEL);
            event.setCancelOrderId(cancelOrderId);
        } finally {
            // 发送委托单
            ringBuffer.publish(sequence);
        }
    }
}

