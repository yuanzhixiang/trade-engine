package com.yuanzhixiang.trade.engine.trade.domain.model.aggregate;

import java.util.Optional;

import org.springframework.transaction.support.TransactionTemplate;

import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountLoginParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountTradeParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountLoginPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountPO;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountLoginEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountLoginValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountType;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountTradeRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.UserRepository;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.SystemException;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;

/**
 * 用户聚合根
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 10:45
 */
public class UserAggregate extends BaseAggregate {

    public UserAggregate(Long id) {
        super(id);
        this.transactionTemplate = ApplicationContextHelper.getBean(TransactionTemplate.class);
        this.userRepository = ApplicationContextHelper.getBean(UserRepository.class);
        this.accountRepository = ApplicationContextHelper.getBean(AccountRepository.class);
        this.accountTradeRepository = ApplicationContextHelper.getBean(AccountTradeRepository.class);
    }

    private TransactionTemplate transactionTemplate;
    private UserRepository userRepository;
    private AccountRepository accountRepository;
    private AccountTradeRepository accountTradeRepository;

    /**
     * 注册登陆账号
     */
    public void registryLoginAccount(AccountLoginValObj accountLoginValObj) {

        // 获取账户
        Optional<FinancialAccountPO> account = getAccount(AccountType.LOGIN);

        // 如果之前没有账户则创建账户
        if (account.isEmpty()) {
            AccountValObj accountValObj = new AccountValObj();
            accountValObj.setUserId(getId());
            accountValObj.setType(AccountType.LOGIN.name());
            accountRepository.postAccount(accountValObj);
            account = getAccount(AccountType.LOGIN);
        }

        // 设置账户 id
        account.ifPresentOrElse(financialAccountPO ->
            accountLoginValObj.setAccountId(financialAccountPO.getId()), () -> {
            ExceptionHelper.throwSystemException("账户创建失败");
        });

        // 校验账户是否已存在
        AccountLoginParam queryParam = new AccountLoginParam();
        queryParam.setLoginAccount(accountLoginValObj.getLoginAccount());
        Optional<FinancialAccountLoginPO> accountLogin = accountRepository.getAccountLogin(queryParam);
        accountLogin.ifPresent(financialAccountLoginPO -> ExceptionHelper.throwSystemException("该登陆名已存在"));

        // 开通登陆账户
        accountRepository.postAccountLogin(accountLoginValObj);
    }

    /**
     * 注册交易账号
     */
    public void registryTradeAccount() {
        Optional<FinancialAccountPO> account = getAccount(AccountType.TRADE);

        if (account.isEmpty()) {
            AccountValObj accountValObj = new AccountValObj();
            accountValObj.setUserId(getId());
            accountValObj.setType(AccountType.TRADE.name());
            accountRepository.postAccount(accountValObj);
            account = getAccount(AccountType.TRADE);
        }

        accountTradeRepository.postAccountTrade(account.get().getId());
    }

    /**
     * 校验登陆密码是否正确
     *
     * @param password 登陆密码
     * @return true 密码正确 / false 密码错误
     */
    public boolean checkPassword(String password) {
        AccountLoginEntity accountLoginEntity = getAccountLoginEntity();
        return accountLoginEntity.checkPassword(password);
    }

    /**
     * 获取登陆账户实体
     *
     * @return 登陆账户实体
     */
    public AccountLoginEntity getAccountLoginEntity() {
        Optional<FinancialAccountPO> account = getAccount(AccountType.LOGIN);
        return account
            .flatMap(financialAccountPO -> {
                AccountLoginParam accountLoginParam = new AccountLoginParam();
                accountLoginParam.setAccountId(financialAccountPO.getId());
                return accountRepository.getAccountLogin(accountLoginParam);
            })
            .map(financialAccountLoginPO -> new AccountLoginEntity(financialAccountLoginPO.getId()))
            .orElseThrow(() -> ExceptionHelper.createException(SystemException.class, "登陆账号不存在"));
    }

    /**
     * 获取交易账户实体
     *
     * @return 交易账户实体
     */
    public AccountTradeEntity getAccountTradeEntity() {
        Optional<FinancialAccountPO> account = getAccount(AccountType.TRADE);
        return account
            .flatMap(financialAccountPO -> {
                AccountTradeParam accountTradeParam = new AccountTradeParam();
                accountTradeParam.setAccountId(financialAccountPO.getId());
                return accountTradeRepository.getAccountTradeOptional(accountTradeParam);
            })
            .map(financialAccountTradePO -> new AccountTradeEntity(financialAccountTradePO.getId()))
            .orElseThrow(() -> ExceptionHelper.createException(SystemException.class, "交易账号不存在"));
    }

    /**
     * 查询用户值对象
     *
     * @return 值对象
     */
    public UserValObj getUserValObj() {
        UserParam userParam = new UserParam();
        userParam.setId(getId());
        return userRepository.getUser(userParam).map(financialUserPO -> {
            UserValObj userValObj = new UserValObj();
            userValObj.setName(financialUserPO.getName());
            userValObj.setType(financialUserPO.getType());
            return userValObj;
        }).orElse(null);
    }

    /**
     * 查询出账户类型为登陆账户的账户
     */
    private Optional<FinancialAccountPO> getAccount(AccountType accountType) {
        AccountParam accountParam = new AccountParam();
        accountParam.setUserId(getId());
        accountParam.setType(accountType.name());
        return accountRepository.getAccount(accountParam);
    }

    /**
     * 删除用户
     */
    public void deleteUser() {
        // 删除用户和登陆账户即可
        transactionTemplate.execute(status -> {
            UserValObj userValObj = getUserValObj();

            if (!userRepository.deleteUser(getId())) {
                ExceptionHelper.throwSystemException("删除用户失败");
            }

            // 如果是正常交易员则删除登陆账户
            if (UserType.TRADER.name().equals(userValObj.getType())) {
                getAccountLoginEntity().deleteAccountLogin();
            }

            // 删除机器人账户
            if (UserType.ROBOT_TRADER.name().equals(userValObj.getType())) {
                userRepository.deleteUserRobot(getId());
            }

            // 删除交易账户
            getAccountTradeEntity().deleteAccountTradeEntity();
            return true;
        });
    }

}
