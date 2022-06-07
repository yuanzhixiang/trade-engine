package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/23 14:36
 */
@Data
public class AccountTradeParam {

    /**
     * 交易账户 id
     */
    private Long id;

    /**
     * 账号 id
     */
    private Long accountId;

}
