package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine;

import lombok.Data;

/**
 * 撤销凭证
 *
 * @author zhixiang.yuan
 * @since 2021/02/15 16:04:46
 */
@Data
public class CancelVoucherValObj {

    /**
     * 委托单 id
     */
    private Long orderId;
    /**
     * 剩余数量
     */
    private Long remainQuantity;
}
