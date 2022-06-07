package com.yuanzhixiang.trade.engine.trade.domain.model.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder.TradeOrderRepository;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.order.TradeOrderEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;

/**
 * 委托单实体工厂
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 11:26
 */
@Component
public class TradeOrderEntityFactory {

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    /**
     * 保存委托单
     *
     * @param tradeOrderValObj 委托单值对象
     * @return 委托单唯一标识
     */
    public TradeOrderEntity postTradeOrder(TradeOrderValObj tradeOrderValObj) {
        Long id = tradeOrderRepository.postTradeOrder(tradeOrderValObj);
        return new TradeOrderEntity(id);
    }

    /**
     * 查询委托单
     *
     * @param id 主键
     * @return
     */
    public TradeOrderEntity getTradeOrder(Long id) {
        FinancialOrderPO financialOrderPO = tradeOrderRepository.getTradeOrder(id);
        return new TradeOrderEntity(financialOrderPO.getId());
    }

}
