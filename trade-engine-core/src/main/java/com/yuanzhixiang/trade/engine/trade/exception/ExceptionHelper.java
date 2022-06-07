package com.yuanzhixiang.trade.engine.trade.exception;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/29 22:04
 */
public class ExceptionHelper {

    /**
     * 对消息进行格式化
     *
     * @param template 消息模版
     * @param params   消息的参数
     * @return 格式化过后的消息
     */
    private static String formatMessage(CharSequence template, Object... params) {
        return StrUtil.format(template, params);
    }

    /**
     * 创建指定的异常类
     *
     * @param exceptionClass 异常类
     * @param template       消息模版
     * @param params         消息参数
     * @param <T>            异常类类型
     * @return 创建出的异常类实例
     */
    public static <T extends BaseException> T createException(
        Class<T> exceptionClass, CharSequence template, Object... params) {
        return ReflectUtil.newInstance(exceptionClass, formatMessage(template, params));
    }

    /**
     * 抛出交易标的异常
     *
     * @param template 消息模版
     * @param params   消息参数
     */
    public static void throwTradeSymbolException(CharSequence template, Object... params) {
        throw new TradeSymbolException(formatMessage(template, params));
    }

    /**
     * 抛出交易域异常
     *
     * @param template 消息模版
     * @param params   消息参数
     */
    public static void throwTradeDomainException(CharSequence template, Object... params) {
        throw new TradeDomainException(formatMessage(template, params));
    }

    /**
     * 抛出系统异常
     *
     * @param template 消息模版
     * @param params   消息参数
     */
    public static void throwSystemException(CharSequence template, Object... params) throws SystemException {
        throw new SystemException(formatMessage(template, params));
    }

}
