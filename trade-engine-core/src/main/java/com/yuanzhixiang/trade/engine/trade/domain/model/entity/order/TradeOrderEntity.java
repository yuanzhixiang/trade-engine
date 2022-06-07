package com.yuanzhixiang.trade.engine.trade.domain.model.entity.order;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder.TradeOrderRepository;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.BaseEntity;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;

/**
 * 委托单实体
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 09:27
 */
public class TradeOrderEntity extends BaseEntity {

    private TradeOrderRepository tradeOrderRepository;

    public TradeOrderEntity(Long id) {
        super(id);
        tradeOrderRepository = ApplicationContextHelper.getBean(TradeOrderRepository.class);
    }

    /**
     * 获取委托单值对象
     *
     * @return 委托单值对象
     */
    public TradeOrderValObj getTradeOrderValObj() {
        FinancialOrderPO tradeOrder = tradeOrderRepository.getTradeOrder(getId());
        TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
        tradeOrderValObj.setAccountTradeId(tradeOrder.getAccountTradeId());
        tradeOrderValObj.setSymbolId(tradeOrder.getSymbolId());
        tradeOrderValObj.setQuantity(tradeOrder.getQuantity());
        tradeOrderValObj.setPrice(tradeOrder.getPrice());
        tradeOrderValObj.setSide(tradeOrder.getSide());
        tradeOrderValObj.setType(tradeOrder.getType());
        tradeOrderValObj.setCompletedQuantity(tradeOrder.getCompletedQuantity());
        tradeOrderValObj.setCanceledQuantity(tradeOrder.getCanceledQuantity());
        return tradeOrderValObj;
    }

    /**
     * 撤销委托单
     *
     * @param canceledQuantity 撤销的数量
     * @return 撤销是否成功
     */
    public boolean cancelOrder(Long canceledQuantity) {
        return tradeOrderRepository.putCanceledQuantity(id, canceledQuantity);
    }

}
