package com.yuanzhixiang.trade.engine.trade.domain.model.valobj;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/10 07:03
 */
@Getter
public class PageResult<T> {

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

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

    /**
     * 创建 PageResult 对象
     *
     * @param current 当前页
     * @param total   总数
     * @param size    每页条数
     * @param records 查询出的记录
     * @param <T>     记录类型
     * @return 页数据封装对象
     */
    public static <T> PageResult<T> getPageResult(Long current, Long total, Long size, List<T> records) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.current = current;
        pageResult.total = total;
        pageResult.size = size;
        pageResult.records = records;
        return pageResult;
    }

    /**
     * 通过原本的创建新的 PageResult
     *
     * @param pageResult    分页查询结果
     * @param recordHandler 对 page 中的元素做映射
     * @param <T>           新元素
     * @return 新的分页结果对象
     */
    public static <T, E> PageResult<T> getPageResult(PageResult<E> pageResult,
        Function<List<E>, List<T>> recordHandler) {
        PageResult<T> newPageResult = new PageResult<>();
        newPageResult.current = pageResult.getCurrent();
        newPageResult.total = pageResult.getTotal();
        newPageResult.size = pageResult.getSize();
        newPageResult.records = recordHandler.apply(pageResult.records);
        return newPageResult;
    }


    public void setRecords(List<T> records) {
        this.records = records;
    }
}
