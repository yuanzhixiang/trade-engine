package com.yuanzhixiang.trade.engine.trade.web.model.request.user;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 用户机器人组创建请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/25 19:06
 */
@Data
public class UserRobotGroupPostRequest {

    /**
     * 机器人数量
     */
    @NotNull(message = "机器人数量不能为空")
    private Long robotQuantity;
    /**
     * 标的编码
     */
    @NotNull(message = "标的编码不能为空")
    private String symbol;
    /**
     * 充值数量
     */
    @NotNull(message = "充值数量不能为空")
    private Long sumQuantity;
    /**
     * 充值总金额
     */
    @NotNull(message = "充值总金额不能为空")
    private Long sumBalance;
}
