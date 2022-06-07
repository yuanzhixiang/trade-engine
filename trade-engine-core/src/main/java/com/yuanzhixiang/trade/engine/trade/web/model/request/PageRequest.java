package com.yuanzhixiang.trade.engine.trade.web.model.request;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/10 10:49
 */
@Data
public class PageRequest extends BaseRequest {

    /**
     * 总数
     */
    private Long total = 0L;
    /**
     * 每页显示条数，默认 10
     */
    private Long size = 10L;

    /**
     * 当前页
     */
    private Long current = 1L;

}
