package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * 交易凭证持久化对象
 *
 * @author zhixiang.yuan
 * @since 2021/02/15 15:00:23
 */
@Data
@TableName("financial_trade_voucher")
public class FinancialTradeVoucherPO extends WithDelPO {

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
    private Long quantity;
    /**
     * 成交价格
     */
    private Double price;
    /**
     * 交易标的
     */
    private Long symbolId;
    /**
     * 成交时间
     */
    private LocalDateTime tradeTime;

}
