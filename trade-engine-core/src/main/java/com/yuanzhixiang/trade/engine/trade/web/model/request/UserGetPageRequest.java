package com.yuanzhixiang.trade.engine.trade.web.model.request;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:26
 */
@Data
public class UserGetPageRequest extends PageRequest {

    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户类型
     */
    private String userType;
}
