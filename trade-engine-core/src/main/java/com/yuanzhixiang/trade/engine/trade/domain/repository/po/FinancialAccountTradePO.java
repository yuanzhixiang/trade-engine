package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * 交易账号持久化对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 19:22
 */
@Data
@TableName("financial_account_trade")
public class FinancialAccountTradePO extends WithDelPO {

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
}
