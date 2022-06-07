package com.yuanzhixiang.trade.engine.trade.web.model.request;

import lombok.Data;

/**
 * 交易标的分页查询请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/10 10:50
 */
@Data
public class TradeSymbolGetPageRequest extends PageRequest {

    /**
     * 标的编码
     */
    private String symbol;

    /**
     * 引擎状态
     */
    private String engineStatus;

}
