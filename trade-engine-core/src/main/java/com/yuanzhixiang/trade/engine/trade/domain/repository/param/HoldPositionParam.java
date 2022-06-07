package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/24 16:36
 */
@Data
public class HoldPositionParam extends PageParam {

    /**
     * 交易账号 id
     */
    private Long accountTradeId;
    /**
     * 交易标的
     */
    private Long symbolId;
}
