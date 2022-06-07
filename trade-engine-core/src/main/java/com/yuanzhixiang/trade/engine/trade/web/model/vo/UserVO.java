package com.yuanzhixiang.trade.engine.trade.web.model.vo;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:25
 */
@Data
public class UserVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户类型
     */
    private String userType;
}
