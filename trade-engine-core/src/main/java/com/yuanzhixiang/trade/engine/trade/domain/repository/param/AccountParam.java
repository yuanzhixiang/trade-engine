package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:45
 */
@Data
public class AccountParam {

    /**
     * 账户主键
     */
    private Long id;
    /**
     * 用户主键
     */
    private Long userId;
    /**
     * 用户类型
     */
    private String type;

}
