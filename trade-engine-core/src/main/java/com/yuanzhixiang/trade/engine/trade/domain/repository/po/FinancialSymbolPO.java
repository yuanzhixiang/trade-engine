package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * 标的持久化对象
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 15:05:47
 */
@Data
@TableName("financial_symbol")
public class FinancialSymbolPO extends WithDelPO {
    /**
     * 标的编码
     */
    private String symbol;
}
