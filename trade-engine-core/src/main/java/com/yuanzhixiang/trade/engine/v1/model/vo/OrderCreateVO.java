package com.yuanzhixiang.trade.engine.v1.model.vo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;

import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator.IsEnum;
import lombok.Data;

/**
 * 用户创建委托单
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 20:59:32
 */
@Data
public class OrderCreateVO {
    /**
     * 用户 id
     */
    @NotNull(message = "用户 id 必须不为 null")
    private Long userId;
    /**
     * 交易标的
     */
    @NotBlank(message = "交易标的不能为空")
    private String symbol;
    /**
     * 交易数量
     */
    @NotNull(message = "交易数量不能为 null")
    @Min(value = 100, message = "交易数量必须大于 100")
    private Long amount;
    /**
     * 交易价格
     */
    @NotNull(message = "交易价格不能为 null")
    @Min(value = 0, message = "交易价格必须大于 0")
    private Double price;

    /**
     * 交易方向，{@link OrderSideEnum}
     */
    @IsEnum(enumClass = OrderSideEnum.class, message = "交易方向错误")
    private String side;

    /**
     * 交易类型，{@link OrderTypeEnum}
     */
    @IsEnum(enumClass = OrderTypeEnum.class, message = "交易类型错误")
    private String type;
}
