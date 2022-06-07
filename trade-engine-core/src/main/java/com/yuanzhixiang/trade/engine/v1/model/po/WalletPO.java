package com.yuanzhixiang.trade.engine.v1.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.BasePO;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 14:43:59
 */
@Data
@TableName("financial_wallet")
public class WalletPO extends BasePO {
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
