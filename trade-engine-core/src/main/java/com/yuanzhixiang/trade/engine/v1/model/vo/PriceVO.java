package com.yuanzhixiang.trade.engine.v1.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/17 13:41:38
 */
@Data
public class PriceVO {
    /**
     * 时间
     */
    private LocalDateTime time;
    /**
     * 价格
     */
    private Double price;
    /**
     * 量能
     */
    private Long volume;
}
