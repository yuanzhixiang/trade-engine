package com.yuanzhixiang.trade.engine.v1.model.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 20:23:22
 */
@Data
public class OrderCancelVO {
    /**
     * 用户 id
     */
    @NotNull(message = "用户 id 不能为 null")
    private Long userId;

    /**
     * 被撤销的委托单
     */
    @NotNull(message = "撤销的委托单 id 不能为 null")
    private Long cancelOrderId;
    /**
     * 交易标的
     */
    @NotBlank(message = "交易标的不能为空")
    private String symbol;

}
