package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade;

import lombok.Data;

/**
 * 持仓记录值对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/02 17:53
 */
@Data
public class HoldPositionValObj {

    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 持有头寸数量
     */
    private Long quantity;
    /**
     * 冻结卖出的头寸数量
     */
    private Long frozenQuantity;

}
