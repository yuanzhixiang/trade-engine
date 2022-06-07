package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core;

import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.TradeVoucherHandlerEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.market.MarketEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;

/**
 * 交易凭证发送器
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 13:45
 */
public class TradeVoucherSenderDisruptor implements TradeVoucherSender {

    private final Disruptor<VoucherEvent> disruptor;

    private final RingBuffer<VoucherEvent> ringBuffer;

    public TradeVoucherSenderDisruptor() {
        disruptor = new Disruptor<>(
            VoucherEvent::new,
            1024 * 1024,
            Executors.defaultThreadFactory(),
            ProducerType.SINGLE,
            new BlockingWaitStrategy()
        );

        // 交易凭证处理器
        disruptor
            .handleEventsWith(ApplicationContextHelper.getBean(TradeVoucherHandlerEntity.class))
            .then(ApplicationContextHelper.getBean(MarketEntity.class))
            .then((event, sequence, endOfBatch) -> event.clear());

        ringBuffer = disruptor.getRingBuffer();

        // 开启消息队列
        disruptor.start();
    }

    @Override
    public void submitTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        long sequence = ringBuffer.next();
        try {
            VoucherEvent orderEvent = ringBuffer.get(sequence);
            orderEvent.setTradeVoucherValObj(tradeVoucherValObj);
        } finally {
            // 发送交易消息
            ringBuffer.publish(sequence);
        }
    }

    @Override
    public void submitCancelVoucher(CancelVoucherValObj cancelVoucherValObj) {
        long sequence = ringBuffer.next();
        try {
            VoucherEvent orderEvent = ringBuffer.get(sequence);
            orderEvent.setCancelVoucherValObj(cancelVoucherValObj);
        } finally {
            // 发送交易消息
            ringBuffer.publish(sequence);
        }
    }

    public void closeSender() {
        disruptor.shutdown();
    }

}
