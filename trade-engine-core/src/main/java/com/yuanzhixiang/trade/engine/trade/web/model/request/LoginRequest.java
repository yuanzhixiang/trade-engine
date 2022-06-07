package com.yuanzhixiang.trade.engine.trade.web.model.request;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:58
 */
@Data
public class LoginRequest {

    /**
     * 登陆账户
     */
    @NotBlank(message = "登陆账户不能为空")
    private String loginAccount;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}
