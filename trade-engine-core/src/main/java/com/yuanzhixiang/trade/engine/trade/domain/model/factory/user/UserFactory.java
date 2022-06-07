package com.yuanzhixiang.trade.engine.trade.domain.model.factory.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.RobotEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.UserRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountLoginParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserRobotParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialUserPO;
import com.yuanzhixiang.trade.engine.trade.utils.MathUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserPostRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserRobotGroupPostRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserRobotPostRequest;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountType;

/**
 * 用户实体工厂
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 10:52
 */
@Component
public class UserFactory {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private List<UserHandler> userHandlerList;
    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 创建用户
     *
     * @param request 请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public UserAggregate postUser(UserPostRequest request) {
        // 创建用户值对象
        UserValObj userValObj = new UserValObj();
        userValObj.setName(request.getUsername());
        userValObj.setType(request.getUserType());

        Long id = userRepository.postUser(userValObj);

        UserAggregate userAggregate = new UserAggregate(id);

        // 开通交易账号
        userAggregate.registryTradeAccount();

        // 给交易账户初始化余额
        AccountTradeEntity accountTradeEntity = userAggregate.getAccountTradeEntity();
        accountTradeEntity.topUpBalance(request.getBalance());

        // 不同用户自定义逻辑
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.userType().equals(userValObj.getType())) {
                userHandler.handle(userAggregate, request);
            }
        }

        return userAggregate;
    }

    /**
     * 创建机器人组
     *
     * @param userRobotGroupPostRequest 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void postUserRobotGroup(UserRobotGroupPostRequest userRobotGroupPostRequest) {
        Long robotQuantity = userRobotGroupPostRequest.getRobotQuantity();
        TradeSymbolAggregate tradeSymbol = tradeSymbolAggregateFactory
            .getTradeSymbol(userRobotGroupPostRequest.getSymbol()).get();

        for (int i = 0; i < robotQuantity; i++) {

            Long quantity;
            Long balance;

            // 如果是创建最后一个机器人则将剩余要充值的数量全部给他
            if (i == robotQuantity - 1) {
                quantity = userRobotGroupPostRequest.getSumQuantity();
                balance = userRobotGroupPostRequest.getSumBalance();
            }
            // 随机一个数量给该机器人
            else {
                // 随机一个要充值的数量
                Long sumQuantity = userRobotGroupPostRequest.getSumQuantity();
                quantity = (long) MathUtil.randomInt(sumQuantity.intValue(), sumQuantity.intValue());
                userRobotGroupPostRequest.setSumQuantity(sumQuantity - quantity);

                // 随机一个要充值的金额
                Long sumBalance = userRobotGroupPostRequest.getSumBalance();
                balance = (long) MathUtil.randomInt(sumBalance.intValue(), sumBalance.intValue());
                userRobotGroupPostRequest.setSumBalance(sumBalance - balance);
            }

            // 创建机器人
            UserRobotPostRequest userRobotPostRequest = new UserRobotPostRequest();
            userRobotPostRequest.setSymbolId(tradeSymbol.getId());
            userRobotPostRequest.setQuantity(quantity);
            userRobotPostRequest
                .setUsername(tradeSymbol.getSymbol() + ": " + UUID.randomUUID().toString().replaceAll("-", ""));
            userRobotPostRequest.setUserType(UserType.ROBOT_TRADER.name());
            userRobotPostRequest.setBalance(balance.doubleValue());
            postUser(userRobotPostRequest);
        }
    }

    /**
     * 按照用户名查询用户
     *
     * @param username 用户名
     * @return 用户聚合根
     */
    public UserAggregate getUser(String username) {
        // 查询登陆账户
        AccountLoginParam accountLoginParam = new AccountLoginParam();
        accountLoginParam.setLoginAccount(username);

        return accountRepository.getAccountLogin(accountLoginParam)
            .flatMap(financialAccountLoginPO -> {
                AccountParam accountParam = new AccountParam();
                accountParam.setId(financialAccountLoginPO.getAccountId());
                accountParam.setType(AccountType.LOGIN.name());
                return accountRepository.getAccount(accountParam);
            })
            .flatMap(financialAccountPO -> {
                UserParam userParam = new UserParam();
                userParam.setId(financialAccountPO.getUserId());
                Optional<FinancialUserPO> user = userRepository.getUser(userParam);
                return user;
            })
            .map(financialUserPO -> new UserAggregate(financialUserPO.getId()))
            .orElse(null);
    }

    /**
     * 按 id 查找用户
     *
     * @param id 用户主键
     * @return 用户聚合根
     */
    public UserAggregate getUser(Long id) {
        UserParam userParam = new UserParam();
        userParam.setId(id);
        return userRepository.getUser(userParam).map(financialUserPO -> new UserAggregate(id)).orElse(null);
    }

    /**
     * 查询机器人实体列表
     *
     * @param symbolId 标的 id
     * @return 机器人实体列表
     */
    public List<RobotEntity> getRobotEntityList(Long symbolId) {
        UserRobotParam userRobotParam = new UserRobotParam();
        userRobotParam.setSymbolId(symbolId);
        return userRepository.getUserRobotList(userRobotParam)
            .stream()
            .map(financialUserRobotPO -> new RobotEntity(financialUserRobotPO.getUserId(),
                financialUserRobotPO.getSymbolId())).collect(Collectors.toList());
    }
}
