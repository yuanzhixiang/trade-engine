package com.yuanzhixiang.trade.engine.trade.web.model.request;

import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator.IsEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 委托单创建请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 23:23
 */
@Data
public class TradeOrderPostRequest extends BaseRequest {

    /**
     * 交易标的编码
     */
    @NotNull(message = "交易标的编码不能为 null")
    private Long symbolId;
    /**
     * 交易数量
     */
    @NotNull(message = "交易数量不能为 null")
    private Long amount;
    /**
     * 交易价格
     */
    @NotNull(message = "交易价格不能为 null")
    private Double price;

    /**
     * 交易方向，{@link OrderSideEnum}
     */
    @IsEnum(message = "交易方向错误", enumClass = OrderSideEnum.class)
    private String side;

    /**
     * 交易类型，{@link OrderTypeEnum}，目前都是限价交易
     */
//    @IsEnum(message = "交易类型错误", enumClass = OrderTypeEnum.class)
    private String type = OrderTypeEnum.LIMIT.name();

}
