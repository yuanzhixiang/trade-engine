package com.yuanzhixiang.trade.engine.v1.model.vo;

import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 17:34:50
 */
@Data
public class WalletVO {
    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 余额
     */
    private Double balance;
}
