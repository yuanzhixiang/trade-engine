package com.yuanzhixiang.trade.engine.trade.web.model.request;

import java.time.LocalDateTime;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineLevel;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/06/20 21:13:04
 */
@Data
public class KMarketGetRequest {
    /**
     * 标的主键
     */
    private Long symbolId;
    /**
     * 行情级别
     */
    private String level = MarketKLineLevel.FIVE_SECOND.name();
    /**
     * 拉取大于等于该时间的行情数据
     */
    private LocalDateTime geTradeTime;
    /**
     * 只拉取最新行情
     */
    private Boolean lastMarket = Boolean.FALSE;
}
