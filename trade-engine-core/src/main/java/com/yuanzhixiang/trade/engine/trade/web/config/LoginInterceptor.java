package com.yuanzhixiang.trade.engine.trade.web.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContextUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContext;
import com.yuanzhixiang.trade.engine.trade.web.model.common.SessionUtil;

/**
 * 登陆拦截器
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 15:28
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    /**
     * 不需要拦截的地址
     */
    private List<String> urlNotFilter = Arrays.asList(
        "/login"
    );

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        // 如果为不需要拦截的地址则直接放过去
        for (String urlPattern : urlNotFilter) {
            if (pathMatcher.match(urlPattern, request.getRequestURI())) {
                return true;
            }
        }

        String header = request.getHeader("user-token");
        UserAggregate user = SessionUtil.getSession(header);
        if (user == null) {
            ExceptionHelper.throwSystemException("用户未登录");
        }

        // 初始化登陆信息
        BusinessContext businessContext = new BusinessContext();
        businessContext.setUser(user);
        BusinessContextUtil.init(businessContext);

        return true;
    }
}
