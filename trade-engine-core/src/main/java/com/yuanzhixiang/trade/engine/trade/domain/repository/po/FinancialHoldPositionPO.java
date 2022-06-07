package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelTimePO;
import lombok.Data;

/**
 * 持仓记录持久化对象
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 11:21:33
 */
@Data
@TableName("financial_hold_position")
public class FinancialHoldPositionPO extends WithDelTimePO {

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 持有头寸数量
     */
    private Long quantity;
    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenQuantity;
}
