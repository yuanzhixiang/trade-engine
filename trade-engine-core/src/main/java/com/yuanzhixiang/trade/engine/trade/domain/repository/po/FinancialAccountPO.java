package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * 账号持久化对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 19:22
 */
@Data
@TableName("financial_account")
public class FinancialAccountPO extends WithDelPO {

    /**
     * 账号类型
     */
    private String type;
    /**
     * 交易员 id
     */
    private Long userId;
}
