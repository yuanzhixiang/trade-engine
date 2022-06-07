package com.yuanzhixiang.trade.engine.v1.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * K 线
 *
 * @author zhixiang.yuan
 * @since 2021/02/19 02:19:33
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
