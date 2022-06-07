package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 15:47
 */
@Data
@TableName("financial_user_robot")
public class FinancialUserRobotPO extends WithDelPO {

    /**
     * 交易员 id
     */
    private Long userId;

    /**
     * 交易员 id
     */
    private Long symbolId;
}
