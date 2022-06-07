package com.yuanzhixiang.trade.engine.trade.domain.model.factory.user;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserPostRequest;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 17:11
 */
public interface UserHandler<T extends UserPostRequest> {

    /**
     * @return 用户类型
     */
    String userType();

    /**
     * 处理特殊的需求
     *
     * @param userAggregate 用户聚合根
     * @param request       特殊请求
     */
    void handle(UserAggregate userAggregate, T request);
}
