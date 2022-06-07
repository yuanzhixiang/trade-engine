package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登陆账户查询条件
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:36
 */
@Data
public class AccountLoginParam {

    /**
     * 主键
     */
    private Long id;
    /**
     * 账户主键
     */
    private Long accountId;
    /**
     * 用户名
     */
    private String loginAccount;

    /**
     * 大于该过期时间的登陆账户背查询出来
     */
    private LocalDateTime gtExpireTime;

}
