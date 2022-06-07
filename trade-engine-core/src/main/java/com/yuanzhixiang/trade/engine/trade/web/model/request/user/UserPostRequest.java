package com.yuanzhixiang.trade.engine.trade.web.model.request.user;

import javax.validation.constraints.NotBlank;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;

import com.yuanzhixiang.trade.engine.trade.web.config.springmvc.validator.IsEnum;
import lombok.Data;

/**
 * 创建用户请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/25 17:08
 */
@Data
public class UserPostRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 用户类型
     */
    @IsEnum(enumClass = UserType.class, message = "用户类型错误")
    private String userType;

    /**
     * 账户余额
     */
    private Double balance;

}
