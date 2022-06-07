package com.yuanzhixiang.trade.engine.trade.web.model.common;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:18
 */
@Data
public class BusinessContext {

    /**
     * 用户聚合根
     */
    private UserAggregate user;

}
