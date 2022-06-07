package com.yuanzhixiang.trade.engine.v1.model.vo;

import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 13:26:23
 */
@Data
public class GameVO {
    /**
     * 本局游戏的用户 id
     */
    private Long userId;
    /**
     * 本局游戏交易标的
     */
    private String symbol;
}
