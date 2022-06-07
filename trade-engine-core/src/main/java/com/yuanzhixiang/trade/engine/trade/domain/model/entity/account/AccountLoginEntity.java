package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import com.yuanzhixiang.trade.engine.trade.domain.model.entity.BaseEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountLoginValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountLoginParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountLoginPO;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.SystemException;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;

import java.time.LocalDateTime;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 17:03
 */
public class AccountLoginEntity extends BaseEntity {

    public AccountLoginEntity(Long id) {
        super(id);
        this.accountRepository = ApplicationContextHelper.getBean(AccountRepository.class);
    }

    private AccountRepository accountRepository;

    /**
     * 校验登陆密码
     *
     * @param password 密码
     * @return true 密码正确 / false 密码错误
     */
    public boolean checkPassword(String password) {
        AccountLoginParam accountLoginParam = new AccountLoginParam();
        accountLoginParam.setId(getId());

        FinancialAccountLoginPO financialAccountLoginPO = accountRepository.getAccountLogin(accountLoginParam)
            .orElseThrow(() -> ExceptionHelper.createException(SystemException.class, "未查找到登陆账户"));

        return password.equals(financialAccountLoginPO.getPassword());
    }

    /**
     * 删除登陆账户
     */
    public void deleteAccountLogin() {
        AccountLoginParam accountLoginParam = new AccountLoginParam();
        accountLoginParam.setId(getId());
        FinancialAccountLoginPO financialAccountLoginPO = accountRepository.getAccountLogin(accountLoginParam)
            .orElseThrow(() -> ExceptionHelper.createException(SystemException.class, "登陆账户不存在"));

        accountRepository.deleteAccount(financialAccountLoginPO.getAccountId());
        accountRepository.deleteAccountLogin(financialAccountLoginPO.getId());
    }

    /**
     * 更新 access token
     */
    public void putAccessToken(String accessToken, LocalDateTime expireTime) {
        AccountLoginValObj accountLoginValObj = new AccountLoginValObj();
        accountLoginValObj.setAccessToken(accessToken);
        accountLoginValObj.setExpireTime(expireTime);
        accountRepository.putAccountLogin(getId(), accountLoginValObj);
    }
}
