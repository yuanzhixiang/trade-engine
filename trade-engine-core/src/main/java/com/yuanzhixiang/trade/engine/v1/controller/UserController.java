package com.yuanzhixiang.trade.engine.v1.controller;

import org.springframework.web.bind.annotation.PostMapping;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.service.UserService;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 11:23:00
 */
public class UserController {

    private UserService userService;

    /**
     * 注册用户
     *
     * @return 用户 id
     */
    @PostMapping("register")
    public Response<Long> register() {
        return Response.success(userService.initUser());
    }
}
