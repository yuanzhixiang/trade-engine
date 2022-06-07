package com.yuanzhixiang.trade.engine.trade.domain.repository.po;

import com.baomidou.mybatisplus.annotation.TableName;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelPO;
import lombok.Data;

/**
 * 用户持久化对象
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/29 19:22
 */
@Data
@TableName("financial_user")
public class FinancialUserPO extends WithDelPO {

    /**
     * 用户类型
     */
    private String type;
    /**
     * 用户名称
     */
    private String name;
}
