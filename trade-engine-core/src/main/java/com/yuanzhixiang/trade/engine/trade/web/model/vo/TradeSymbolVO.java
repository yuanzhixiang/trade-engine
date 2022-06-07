package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import lombok.Data;

/**
 * 交易标的视图
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 23:03
 */
@Data
public class TradeSymbolVO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 标的编码
     */
    private String symbol;

    /**
     * 交易引擎状态
     */
    private String engineStatus;
}
