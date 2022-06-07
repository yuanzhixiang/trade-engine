package com.yuanzhixiang.trade.engine.trade.web;

import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContext;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContextUtil;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/24 18:49
 */
@RestController
@RequestMapping("/api")
public class OtherController {

    /**
     * 查询当前用户，给前端防止报错用的
     */
    @GetMapping("/currentUser")
    public Response currentUser() {
        BusinessContext businessContext = BusinessContextUtil.get();
        UserAggregate user = businessContext.getUser();
        if (user != null) {
            HashMap result = new HashMap<>();
            result.put("userid", user.getId());
            result.put("name", user.getUserValObj().getName());
            return Response.success(result);
        }

        return Response.success();
    }

}
