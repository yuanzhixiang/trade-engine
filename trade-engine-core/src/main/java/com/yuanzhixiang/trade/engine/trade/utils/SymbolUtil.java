package com.yuanzhixiang.trade.engine.trade.utils;

import java.util.Locale;
import java.util.UUID;

/**
 * 标的编码工具
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/09 13:56
 */
public class SymbolUtil {

    /**
     * 随机生成标的编码
     */
    public static String generateSymbol() {
        return UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 6).toUpperCase(Locale.ROOT);
    }
}
