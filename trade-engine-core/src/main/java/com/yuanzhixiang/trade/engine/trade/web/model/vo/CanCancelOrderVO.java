package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import java.time.LocalDateTime;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/24 17:49
 */
@Data
public class CanCancelOrderVO {

    private Long id;

    /**
     * 标的名称
     */
    private String symbol;
    /**
     * 标的 id
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
     * 交易方向，{@link OrderSideEnum}
     */
    private String side;

    /**
     * 成交的数量
     */
    private Long completedQuantity;
    /**
     * 撤销的数量
     */
    private Long canceledQuantity;
    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;
}
