package com.yuanzhixiang.trade.engine.trade.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.PageParam;

/**
 * 处理 MybatisPlus 工具类
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/10 07:05
 */
public class PageUtil {

    /**
     * 将 MybatisPlus 中的页对象转为系统中的页对象
     *
     * @param page MybatisPlus 中的页对象
     * @param <T>  值类型
     * @return 系统中的页对象
     */
    public static <T> PageResult<T> pageToPageResult(Page<T> page) {
        return PageResult.getPageResult(page.getCurrent(), page.getTotal(), page.getSize(), page.getRecords());
    }

    /**
     * 将查询参数中的分页部分剥离出来
     *
     * @param pageParam 查询参数
     * @return 分页参数
     */
    public static <T> Page<T> pageParamToPage(PageParam pageParam) {
        Page<T> page = new Page<>();
        page.setTotal(pageParam.getTotal());
        page.setSize(pageParam.getSize());
        page.setCurrent(pageParam.getCurrent());
        return page;
    }
}
