package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhixiang.yuan
 * @since 2021/06/20 21:16:29
 */
@Data
public class MarketKLineValObj {

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
