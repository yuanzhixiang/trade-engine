package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import lombok.Data;

/**
 * 委托单持久化对象
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 21:06:19
 */
@Data
@TableName("financial_order")
public class FinancialOrderPO extends WithDelPO {

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 委托数量
     */
    private Long quantity;
    /**
     * 委托价格
     */
    private Double price;
    /**
     * 交易方向，{@link OrderSideEnum}
     */
    private String side;

    /**
     * 交易类型，{@link OrderTypeEnum}
     */
    private String type;

    /**
     * 成交的数量
     */
    private Long completedQuantity;

    /**
     * 撤销数量
     */
    private Long canceledQuantity;
}
