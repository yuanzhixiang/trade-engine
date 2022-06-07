package com.yuanzhixiang.trade.engine.trade.domain.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountLoginParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountLoginPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountPO;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountLoginValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialAccountLoginMapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialAccountMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 账户服务仓储层
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/12 11:07
 */
@Repository
public class AccountRepository {

    @Autowired
    private FinancialAccountMapper financialAccountMapper;

    @Autowired
    private FinancialAccountLoginMapper financialAccountLoginMapper;

    /**
     * 创建账号
     *
     * @param accountValObj 账号值对象
     * @return 账号主键
     */
    public Long postAccount(AccountValObj accountValObj) {
        FinancialAccountPO financialAccountPO = new FinancialAccountPO();
        financialAccountPO.setType(accountValObj.getType());
        financialAccountPO.setUserId(accountValObj.getUserId());
        financialAccountMapper.insert(financialAccountPO);
        return financialAccountPO.getId();
    }

    /**
     * 查询账户
     *
     * @param accountParam 账户查询条件
     * @return 账户信息
     */
    public Optional<FinancialAccountPO> getAccount(AccountParam accountParam) {
        LambdaQueryWrapper<FinancialAccountPO> condition = accountParamToCondition(accountParam);
        return Optional.ofNullable(financialAccountMapper.selectOne(condition));
    }

    private LambdaQueryWrapper<FinancialAccountPO> accountParamToCondition(AccountParam accountParam) {
        LambdaQueryWrapper<FinancialAccountPO> condition = new LambdaQueryWrapper<>();
        condition.eq(accountParam.getId() != null, FinancialAccountPO::getId, accountParam.getId());
        condition.eq(accountParam.getUserId() != null, FinancialAccountPO::getUserId, accountParam.getUserId());
        condition.eq(StrUtil.isNotBlank(accountParam.getType()), FinancialAccountPO::getType, accountParam.getType());
        return condition;
    }

    /**
     * 删除账户
     *
     * @param accountId 账户 id
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteAccount(Long accountId) {
        return financialAccountMapper.deleteById(accountId) == 1;
    }

    // 以下是处理登陆账户的代码

    /**
     * 创建登陆账户
     *
     * @param accountLoginValObj 账户值对象
     * @return 登陆账户主键
     */
    public Long postAccountLogin(AccountLoginValObj accountLoginValObj) {
        FinancialAccountLoginPO financialAccountLoginPO = new FinancialAccountLoginPO();
        financialAccountLoginPO.setAccountId(accountLoginValObj.getAccountId());
        financialAccountLoginPO.setLoginAccount(accountLoginValObj.getLoginAccount());
        financialAccountLoginPO.setPassword(accountLoginValObj.getPassword());
        financialAccountLoginMapper.insert(financialAccountLoginPO);
        return financialAccountLoginPO.getId();
    }

    /**
     * 更新账户登陆时间
     *
     * @param accountLoginValObj 账户值对象
     */
    public void putAccountLogin(Long id, AccountLoginValObj accountLoginValObj) {
        FinancialAccountLoginPO financialAccountLoginPO = new FinancialAccountLoginPO();
        financialAccountLoginPO.setId(id);
        financialAccountLoginPO.setAccessToken(accountLoginValObj.getAccessToken());
        financialAccountLoginPO.setExpireTime(accountLoginValObj.getExpireTime());
        financialAccountLoginMapper.updateById(financialAccountLoginPO);
    }

    /**
     * 查询登陆账户
     *
     * @param accountLoginParam 查询登陆账户条件
     * @return 登陆账户信息
     */
    public Optional<FinancialAccountLoginPO> getAccountLogin(AccountLoginParam accountLoginParam) {
        LambdaQueryWrapper<FinancialAccountLoginPO> condition = getCondition(accountLoginParam);
        return Optional.ofNullable(financialAccountLoginMapper.selectOne(condition));
    }

    /**
     * 拼接查询条件
     *
     * @param accountLoginParam 查询参数
     * @return 拼接出的查询参数
     */
    private LambdaQueryWrapper<FinancialAccountLoginPO> getCondition(AccountLoginParam accountLoginParam) {
        LambdaQueryWrapper<FinancialAccountLoginPO> condition = new LambdaQueryWrapper<>();
        condition.eq(accountLoginParam.getId() != null, FinancialAccountLoginPO::getId, accountLoginParam.getId());
        condition.eq(accountLoginParam.getAccountId() != null,
                FinancialAccountLoginPO::getAccountId, accountLoginParam.getAccountId());
        condition.eq(StrUtil.isNotBlank(accountLoginParam.getLoginAccount()),
                FinancialAccountLoginPO::getLoginAccount, accountLoginParam.getLoginAccount());
        condition.gt(accountLoginParam.getGtExpireTime() != null, FinancialAccountLoginPO::getExpireTime,
                accountLoginParam.getGtExpireTime());
        return condition;
    }

    /**
     * 查询登陆账户列表
     *
     * @return 登陆账户列表
     */
    public List<FinancialAccountLoginPO> getAccountLoginList(AccountLoginParam accountLoginParam) {
        LambdaQueryWrapper<FinancialAccountLoginPO> condition = getCondition(accountLoginParam);
        return financialAccountLoginMapper.selectList(condition);
    }

    /**
     * 删除登陆账户
     *
     * @param accountLoginId 登陆账户 id
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteAccountLogin(Long accountLoginId) {
        return financialAccountLoginMapper.deleteById(accountLoginId) == 1;
    }


}
