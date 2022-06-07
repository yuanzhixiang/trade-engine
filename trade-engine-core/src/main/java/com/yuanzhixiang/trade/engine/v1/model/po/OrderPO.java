package com.yuanzhixiang.trade.engine.v1.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.BasePO;
import lombok.Data;

/**
 * 委托单
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 21:06:19
 */
@Data
@TableName("financial_order")
public class OrderPO extends BasePO {

    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 交易标的
     */
    private String symbol;
    /**
     * 委托数量
     */
    private Long amount;
    /**
     * 委托价格
     */
    private Double price;
    /**
     * 交易动作，{@link OrderActionEnum}
     */
    private String action;

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
    private Long tradeAmount;

    /**
     * 撤销数量
     */
    private Long cancelAmount;


    /**
     * 被撤销的委托单，只有当 action 为 cancel 的时候才会填入这个
     */
    private Long cancelOrderId;
}
