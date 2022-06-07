package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade;

import lombok.Data;

/**
 * 交易账户值对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/01 13:27
 */
@Data
public class AccountTradeValObj {

    /**
     * 账号 id
     */
    private Long accountId;
    /**
     * 余额
     */
    private Double balance;
    /**
     * 冻结的资金
     */
    private Double frozenFund;
    /**
     * 乐观锁
     */
    private Long version;

}
