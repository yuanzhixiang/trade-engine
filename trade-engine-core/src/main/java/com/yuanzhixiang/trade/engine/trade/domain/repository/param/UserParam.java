package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * 用户参数
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:50
 */
@Data
public class UserParam extends PageParam {

    /**
     * 主键
     */
    private Long id;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户类型
     */
    private String type;
}
