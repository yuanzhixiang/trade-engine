package com.yuanzhixiang.trade.engine.trade.domain.model.factory.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;
import com.yuanzhixiang.trade.engine.trade.domain.repository.UserRepository;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserRobotPostRequest;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/25 17:19
 */
@Component
public class UserRobotHandler implements UserHandler<UserRobotPostRequest> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String userType() {
        return UserType.ROBOT_TRADER.name();
    }

    @Override
    public void handle(UserAggregate userAggregate, UserRobotPostRequest request) {
        if (request.getQuantity() == null) {
            request.setQuantity(0L);
        }

        userRepository.postUserRobot(userAggregate.getId(), request.getSymbolId());

        // 给账户充值证券
        AccountTradeEntity accountTradeEntity = userAggregate.getAccountTradeEntity();
        accountTradeEntity.topUpSymbol(request.getSymbolId(), request.getQuantity());
    }
}
