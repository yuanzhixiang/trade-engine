package com.yuanzhixiang.trade.engine.trade.domain.repository.param;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/10 11:02
 */
@Data
public class PageParam {

    /**
     * 总数
     */
    private Long total = 0L;
    /**
     * 每页显示条数，默认 10
     */
    private Long size = 1000L;

    /**
     * 当前页
     */
    private Long current = 1L;

}
