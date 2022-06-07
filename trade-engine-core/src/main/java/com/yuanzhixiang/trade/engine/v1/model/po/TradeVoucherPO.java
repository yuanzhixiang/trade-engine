package com.yuanzhixiang.trade.engine.v1.model.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.BasePO;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 15:00:23
 */
@Data
@TableName("financial_trade_voucher")
public class TradeVoucherPO extends BasePO {

    /**
     * 挂单 id，原本挂在交易引擎上的订单
     */
    private Long makerId;

    /**
     * 吃单 id，是指吃掉 maker 的订单
     */
    private Long takeId;
    /**
     * 吃单的买卖方向
     */
    private String takerSide;
    /**
     * 成交数量
     */
    private Long amount;
    /**
     * 成交价格
     */
    private Double price;
    /**
     * 交易标的
     */
    private String symbol;
    /**
     * 成交时间
     */
    private LocalDateTime tradeTime;

}
