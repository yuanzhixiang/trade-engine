package com.yuanzhixiang.trade.engine.trade.web.model.common;

import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session 工具
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/13 14:27
 */
public class SessionUtil {

    /**
     * 存放 session 信息
     */
    private static final ConcurrentHashMap<String, Object> SESSION = new ConcurrentHashMap<>();

    /**
     * 注册 session
     *
     * @param session 登陆信息
     * @return 随机出的 uuid
     */
    public static String registrySession(UserAggregate session) {
        String key = UUID.randomUUID().toString();
        SESSION.put(key, session);
        return key;
    }

    /**
     * 注册 session
     *
     * @param session 登陆信息
     */
    public static void registrySession(String key, UserAggregate session) {
        SESSION.put(key, session);
    }

    /**
     * 从 session 中找出登陆信息
     *
     * @param key 用来找信息的 key
     * @return 登陆信息
     */
    public static <T> T getSession(String key) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        return (T) SESSION.get(key);
    }
}
