package com.yuanzhixiang.trade.engine.trade.web.model.request;

import lombok.Data;

/**
 * 交易标的创建请求
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 21:27
 */
@Data
public class TradeSymbolPostRequest extends BaseRequest {
    /**
     * 标的名称
     */
    private String name;
}
