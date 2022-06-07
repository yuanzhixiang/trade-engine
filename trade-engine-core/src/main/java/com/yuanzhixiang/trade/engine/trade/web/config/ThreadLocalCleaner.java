package com.yuanzhixiang.trade.engine.trade.web.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContextUtil;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:15
 */
@Component
public class ThreadLocalCleaner implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            BusinessContextUtil.remove();
            chain.doFilter(request, response);
        } finally {
            BusinessContextUtil.remove();
        }
    }
}
