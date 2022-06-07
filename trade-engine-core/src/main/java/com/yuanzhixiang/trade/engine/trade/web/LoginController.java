package com.yuanzhixiang.trade.engine.trade.web;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.trade.application.AccountLoginEntityApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.user.UserFactory;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.web.model.request.LoginRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 登陆相关接口
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 16:26
 */
@Slf4j
@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private AccountLoginEntityApplication accountLoginEntityApplication;

    /**
     * 用户登录
     *
     * @param loginRequest 登陆请求参数
     */
    @PostMapping
    public Response<LoginVO> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserAggregate userAggregate = userFactory.getUser(loginRequest.getLoginAccount());
        if (userAggregate == null) {
            ExceptionHelper.throwSystemException("用户不存在");
        }
        boolean result = userAggregate.checkPassword(loginRequest.getPassword());
        if (result) {
            LoginVO loginVO = new LoginVO();
            loginVO.setUserToken(accountLoginEntityApplication.registrySession(userAggregate));
            return Response.success(loginVO);
        } else {
            ExceptionHelper.throwSystemException("账号密码错误");
        }

        return Response.success(new LoginVO());
    }
}
