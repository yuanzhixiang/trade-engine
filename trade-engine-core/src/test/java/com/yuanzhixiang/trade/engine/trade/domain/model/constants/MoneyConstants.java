package com.yuanzhixiang.trade.engine.trade.domain.model.constants;

/**
 * 关于钱的常量
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 23:49
 */
public class MoneyConstants {

    /**
     * 两个 double 钱之前的浮动值小于这个数字都认为是正常的，我系统里面的数据都是两位小数，所以这里忽略两位小数后面的所有值
     */
    public static final double DOUBLE_DELTA = 0.001;
}
