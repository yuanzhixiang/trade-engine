package com.yuanzhixiang.trade.engine.v1.model.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 持仓
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 11:43:09
 */
@Data
public class HoldPositionVO {

    /**
     * 用户 id
     */
    @NotNull(message = "用户 id 不能为 null")
    private Long userId;
    /**
     * 持有标的
     */
    @NotBlank(message = "标的编码不能为空")
    private String symbol;
    /**
     * 持有头寸数量
     */
    @Min(value = 100, message = "持仓股数不能小于 100")
    private Long positionAmount;
    /**
     * 成本价
     */
    @Min(value = 1, message = "持仓成本不能小于 1 元")
    private Double costPrice;

    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenSellAmount;

}
