package com.yuanzhixiang.trade.engine.v1.model.bo;

import lombok.Data;

/**
 * 钱包
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 17:34:34
 */
@Data
public class WalletBO {
    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 余额
     */
    private Double balance;

    /**
     * 冻结的资金
     */
    private Double frozenFund;
}
