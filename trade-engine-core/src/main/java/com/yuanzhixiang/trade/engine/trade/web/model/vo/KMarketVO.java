package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhixiang.yuan
 * @since 2021/06/20 21:11:22
 */
@Data
public class KMarketVO {

    /**
     * 时间
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
     * 最低价
     */
    private Double low;
    /**
     * 最高价
     */
    private Double high;
    /**
     * 量能
     */
    private Long volume;
}
