package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine;


import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;

import lombok.Data;

/**
 * 需要用引擎处理的委托单
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 12:57
 */
@Data
public class EngineOrderValObj {

    /**
     * 委托单 id
     */
    private Long id;

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 交易数量
     */
    private Long quantity;
    /**
     * 交易价格
     */
    private Double price;
    /**
     * 剩余数量
     */
    private Long remainQuantity;

    /**
     * 交易方向，{@link OrderSideEnum}
     */
    private String side;

    /**
     * 交易类型，{@link OrderTypeEnum}
     */
    private String type;

    /**
     * 订单创建时间
     */
    private Long createTime;

    /**
     * 是否被撤单
     */
    private Boolean cancel = false;
}
