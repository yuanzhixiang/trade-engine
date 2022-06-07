package com.yuanzhixiang.trade.engine.v1.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.BasePO;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 15:05:47
 */
@Data
@TableName("financial_symbol")
public class SymbolPO extends BasePO {
    /**
     * 持有标的
     */
    private String symbol;
    /**
     * 最新价格
     */
    private Double latestPrice;
}
