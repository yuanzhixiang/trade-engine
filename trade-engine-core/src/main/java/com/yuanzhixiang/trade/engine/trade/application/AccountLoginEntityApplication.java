package com.yuanzhixiang.trade.engine.trade.application;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountLoginEntity;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.UserRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountLoginParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountLoginPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialUserPO;
import com.yuanzhixiang.trade.engine.trade.web.model.common.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 登陆账户实体服务
 *
 * @author zhixiang.yuan
 * @since 2021/06/22 01:46:37
 */
@Component
public class AccountLoginEntityApplication {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 初始化 session util
     */
    public void initLocalSession() {
        AccountLoginParam accountLoginParam = new AccountLoginParam();
        // 到当前时间还未过期的记录则查询出来
        accountLoginParam.setGtExpireTime(LocalDateTime.now());
        List<FinancialAccountLoginPO> accountLoginList = accountRepository.getAccountLoginList(accountLoginParam);

        for (FinancialAccountLoginPO financialAccountLoginPO : accountLoginList) {
            // 查询账户
            AccountParam accountParam = new AccountParam();
            accountParam.setId(financialAccountLoginPO.getAccountId());
            Optional<FinancialAccountPO> account = accountRepository.getAccount(accountParam);
            if (account.isEmpty()) {
                continue;
            }

            // 查询用户
            UserParam userParam = new UserParam();
            userParam.setId(account.get().getUserId());
            Optional<FinancialUserPO> user = userRepository.getUser(userParam);
            if (user.isEmpty()) {
                continue;
            }

            // 注册登陆信息
            SessionUtil.registrySession(financialAccountLoginPO.getAccessToken(), new UserAggregate(user.get().getId()));
        }
    }

    /**
     * 注册 session 信息
     *
     * @param userAggregate 用户聚合根
     * @return accessToken
     */
    public String registrySession(UserAggregate userAggregate) {
        String key = UUID.randomUUID().toString();
        SessionUtil.registrySession(key, userAggregate);

        AccountLoginEntity accountLoginEntity = userAggregate.getAccountLoginEntity();
        // 过期时间设置为一天
        accountLoginEntity.putAccessToken(key, LocalDateTime.now().plusDays(1));
        return key;
    }
}
