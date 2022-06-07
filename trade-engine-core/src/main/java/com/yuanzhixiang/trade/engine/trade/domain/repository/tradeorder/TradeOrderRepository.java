package com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeOrderParam;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialOrderMapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;

/**
 * 委托单存储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 09:55
 */
@Repository
public class TradeOrderRepository {

    private final FinancialOrderMapper financialOrderMapper;

    private final TradeOrderPOConverter tradeOrderPOConverter;

    public TradeOrderRepository(
        FinancialOrderMapper financialOrderMapper,
        TradeOrderPOConverter tradeOrderPOConverter
    ) {
        this.financialOrderMapper = financialOrderMapper;
        this.tradeOrderPOConverter = tradeOrderPOConverter;
    }

    /**
     * 保存委托单
     *
     * @param tradeOrderValObj 委托单值对象
     * @return 委托单唯一标识
     */
    public Long postTradeOrder(TradeOrderValObj tradeOrderValObj) {
        FinancialOrderPO financialOrderPO = tradeOrderPOConverter.oneToOther(tradeOrderValObj);
        financialOrderPO.setCompletedQuantity(0L);
        financialOrderPO.setCanceledQuantity(0L);
        financialOrderMapper.insert(financialOrderPO);

        return financialOrderPO.getId();
    }

    /**
     * 查询指定交易账户下的可撤单
     *
     * @param tradeOrderParam 查询条件
     * @return 可撤单列表
     */
    public List<FinancialOrderPO> getCanCancelOrder(TradeOrderParam tradeOrderParam) {
        LambdaQueryWrapper<FinancialOrderPO> condition = paramToCondition(tradeOrderParam);
        condition.last("and quantity != completed_quantity + canceled_quantity");
        return financialOrderMapper.selectList(condition);
    }

    public LambdaQueryWrapper<FinancialOrderPO> paramToCondition(TradeOrderParam tradeOrderParam) {
        LambdaQueryWrapper<FinancialOrderPO> condition = new LambdaQueryWrapper<>();
        condition.eq(tradeOrderParam.getAccountTradeId() != null,
            FinancialOrderPO::getAccountTradeId, tradeOrderParam.getAccountTradeId());
        condition.eq(tradeOrderParam.getSymbolId() != null,
            FinancialOrderPO::getSymbolId, tradeOrderParam.getSymbolId());
        return condition;
    }

    /**
     * 查询委托单
     *
     * @param id 主键
     */
    public FinancialOrderPO getTradeOrder(Long id) {
        return financialOrderMapper.selectById(id);
    }

    /**
     * 修改已完成的交易数量
     *
     * @param id                主键
     * @param completedQuantity 已完成的数量
     * @return true 修改成功/ false 修改失败
     */
    public boolean putCompletedQuantity(Long id, Long completedQuantity, Long version) {
        FinancialOrderPO financialOrderPO = new FinancialOrderPO();
        financialOrderPO.setId(id);
        financialOrderPO.setCompletedQuantity(completedQuantity);
        financialOrderPO.setVersion(version);
        return financialOrderMapper.updateById(financialOrderPO) == 1;
    }

    /**
     * 修改撤销数量
     *
     * @param id               主键
     * @param canceledQuantity 需要撤销的数量
     * @return true 修改成功/ false 修改失败
     */
    public boolean putCanceledQuantity(Long id, Long canceledQuantity) {
        FinancialOrderPO financialOrderPO = new FinancialOrderPO();
        financialOrderPO.setId(id);
        financialOrderPO.setCanceledQuantity(canceledQuantity);
        return financialOrderMapper.updateById(financialOrderPO) == 1;
    }
}
