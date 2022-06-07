package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import lombok.Data;

/**
 * 交易账户视图
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/24 11:37
 */
@Data
public class AccountTradeVO {

    /**
     * 余额
     */
    private Double balance;

    /**
     * 冻结的资金
     */
    private Double frozenFund;
}
