package com.yuanzhixiang.trade.engine.trade.web.model.common;

/**
 * 业务上下文
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:17
 */
public class BusinessContextUtil {

    private static final ThreadLocal<BusinessContext> threadLocal = new ThreadLocal<>();

    /**
     * 初始化
     *
     * @param businessContext 放入业务上下文
     */
    public static void init(BusinessContext businessContext) {
        threadLocal.set(businessContext);
    }

    /**
     * 获取
     *
     * @return 业务上下文
     */
    public static BusinessContext get() {
        return threadLocal.get();
    }

    /**
     * 移除
     */
    public static void remove() {
        threadLocal.remove();
    }

}
