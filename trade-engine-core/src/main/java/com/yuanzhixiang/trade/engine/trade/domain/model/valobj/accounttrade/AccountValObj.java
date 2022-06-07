package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade;

import lombok.Data;

/**
 * 账号值对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 14:25
 */
@Data
public class AccountValObj {

    /**
     * 用户主键
     */
    private Long userId;
    /**
     * 账号类型
     */
    private String type;

}
