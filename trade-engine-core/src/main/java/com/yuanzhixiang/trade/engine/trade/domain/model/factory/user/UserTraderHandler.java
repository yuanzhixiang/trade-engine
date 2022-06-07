package com.yuanzhixiang.trade.engine.trade.domain.model.factory.user;

import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountLoginValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserTraderPostRequest;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 17:14
 */
@Component
public class UserTraderHandler implements UserHandler<UserTraderPostRequest> {

    @Override
    public String userType() {
        return UserType.TRADER.name();
    }

    @Override
    public void handle(UserAggregate userAggregate, UserTraderPostRequest request) {
        // 只有真实的交易员才需要开通登陆账号
        // 创建登陆账号值对象
        AccountLoginValObj accountLoginValObj = new AccountLoginValObj();
        accountLoginValObj.setLoginAccount(request.getLoginAccount());
        accountLoginValObj.setPassword(request.getPassword());

        // 开通登陆账号
        userAggregate.registryLoginAccount(accountLoginValObj);
    }
}
