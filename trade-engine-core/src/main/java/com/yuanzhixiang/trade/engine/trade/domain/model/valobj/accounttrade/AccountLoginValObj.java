package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 14:49
 */
@Data
public class AccountLoginValObj {

    /**
     * 账号 id
     */
    private Long accountId;
    /**
     * 登陆账户
     */
    private String loginAccount;
    /**
     * 密码
     */
    private String password;

    /**
     * 是否已登陆
     */
    private String accessToken;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}
