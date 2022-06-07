package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 02:06
 */
@Data
@TableName("financial_account_login")
public class FinancialAccountLoginPO extends WithDelPO {

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
