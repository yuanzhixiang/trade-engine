package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * k 线查询条件
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 20:17:19
 */
@Data
public class MarketKLineParam {
    /**
     * 标的主键
     */
    private Long symbolId;
    /**
     * 级别
     */
    private String level;
    /**
     * 拉取大于等于该时间的行情数据
     */
    private LocalDateTime geTradeTime;
    /**
     * 只拉取最新行情
     */
    private Boolean lastMarket = Boolean.FALSE;
}
