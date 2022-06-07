package com.yuanzhixiang.trade.engine.v1.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.BasePO;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:21:33
 */
@Data
@TableName("financial_hold_position")
public class HoldPositionPO extends BasePO {

    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 持有标的
     */
    private String symbol;
    /**
     * 持有头寸数量
     */
    private Long positionAmount;
    /**
     * 成本价
     */
    private Double costPrice;
    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenSellAmount;
}
