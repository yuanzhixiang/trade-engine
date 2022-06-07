package com.yuanzhixiang.trade.engine.trade.web.model.request.user;

import lombok.Data;

/**
 * 创建机器人用户请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/25 17:09
 */
@Data
public class UserRobotPostRequest extends UserPostRequest {

    /**
     * 需要充值的标的 id
     */
    private Long symbolId;

    /**
     * 充值的数量
     */
    private Long quantity;
}
