package com.yuanzhixiang.trade.engine.trade.web.model.request.user;

import lombok.Data;

/**
 * 创建真实交易员用户请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/13 11:04
 */
@Data
public class UserTraderPostRequest extends UserPostRequest {

    /**
     * 登陆账户
     */
    private String loginAccount;

    /**
     * 密码
     */
    private String password;
}
