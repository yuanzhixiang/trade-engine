package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行情 k 线持久化对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 20:29
 */
@Data
@TableName("financial_market_k_line")
public class FinancialMarketKLinePO extends WithDelPO {

    /**
     * 交易标的
     */
    private Long symbolId;

    /**
     * 级别
     */
    private String level;
    /**
     * 成交时间
     */
    private LocalDateTime time;
    /**
     * 开盘价
     */
    private Double open;
    /**
     * 收盘价
     */
    private Double close;
    /**
     * 最高价
     */
    private Double high;
    /**
     * 最低价
     */
    private Double low;

    /**
     * 成交量
     */
    private Long volume;

}
