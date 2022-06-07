package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.constants.MoneyConstants;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.AccountTradeEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.ThreadUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.trade.domain.repository.HoldPositionRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialHoldPositionPO;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;

import cn.hutool.core.collection.CollectionUtil;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountTradeValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.HoldPositionValObj;

import lombok.extern.slf4j.Slf4j;

/**
 * 交易账户单元测试，todo 部分代码可以抽象重构
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 23:40
 */
@Slf4j
public class AccountTradeEntityTest extends BaseApplication {

    @Autowired
    private HoldPositionRepository holdPositionRepository;

    @Autowired
    private AccountTradeEntityFactory accountTradeEntityFactory;

    public static void assertAccount(AccountTradeEntity accountTradeEntity, List<Long> symbolIdList) {
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertTrue("账户余额不能小于 0", accountTradeValObj.getBalance() >= 0);
        Assert.assertTrue("账户冻结余额不能小于 0", accountTradeValObj.getFrozenFund() >= 0);

        if (CollectionUtil.isEmpty(symbolIdList)) {
            return;
        }
        for (Long symbolId : symbolIdList) {
            HoldPositionValObj holdPosition = accountTradeEntity.getHoldPositionQuantity(symbolId);
            if (holdPosition == null) {
                continue;
            }
            Assert.assertTrue("账户持仓标的数不能小于 0", holdPosition.getQuantity() >= 0);
            Assert.assertTrue("账户持仓标的冷冻数不能小于 0", holdPosition.getFrozenQuantity() >= 0);
        }
    }

    /**
     * 单个账户不能对一个标的有两个持仓记录
     */
    @Test(expected = DuplicateKeyException.class)
    public void _0001() {
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();

        Long symbolId = TradeSymbolUtil.generateSymbolId();

        FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
        financialHoldPositionPO.setAccountTradeId(accountTradeEntity.getId());
        financialHoldPositionPO.setSymbolId(symbolId);
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        // 创建第二条相同持仓会报 DuplicateKeyException 异常
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 单账户对同一标的的持仓记录可以删除多次
     */
    @Test
    public void _0002() throws InterruptedException {
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();

        Long symbolId = TradeSymbolUtil.generateSymbolId();

        FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
        financialHoldPositionPO.setAccountTradeId(accountTradeEntity.getId());
        financialHoldPositionPO.setSymbolId(symbolId);
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        // 删除持仓
        holdPositionRepository.deleteHoldPosition(financialHoldPositionPO.getId());

        // 删除后再插入则不报错
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        // 目前一秒只能删一条，所以这里需要休息一秒
        TimeUnit.SECONDS.sleep(1);

        // 删除持仓
        holdPositionRepository.deleteHoldPosition(financialHoldPositionPO.getId());

        // 删除后再插入则不报错
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        // 目前一秒只能删一条，所以这里需要休息一秒
        TimeUnit.SECONDS.sleep(1);

        // 删除持仓
        holdPositionRepository.deleteHoldPosition(financialHoldPositionPO.getId());

        // 删除后再插入则不报错
        holdPositionRepository.postHoldPosition(financialHoldPositionPO);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 给账户充值，验证账户余额不出错
     */
    @Test
    public void _0101() {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();

        // 将金额充值到账户
        double sumOfMoney = ThreadUtil.syncTaskProvideDouble(accountTradeEntity::topUpBalance);

        // 判断总金额是否正确
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(sumOfMoney, accountTradeValObj.getBalance(), MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 给账户并发充值，验证账户余额不出错
     */
    @Test
    public void _0102() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();

        // 给账户充值
        double sumOfMoney = ThreadUtil.asyncTaskProvideDouble(accountTradeEntity::topUpBalance);

        // 验证账户余额不错错
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(sumOfMoney, accountTradeValObj.getBalance(), MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 解冻资金
     */
    @Test
    public void _0103() {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000D;
        accountTradeEntity.topUpBalance(initBalance);

        // 冻结资金
        double sumOfFrozenMoney = ThreadUtil.syncTaskProvideDouble(accountTradeEntity::frozenFund);

        // 判断总的冻结金额是否正确
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(sumOfFrozenMoney, accountTradeValObj.getFrozenFund(), MoneyConstants.DOUBLE_DELTA);
        Assert
            .assertEquals(initBalance - sumOfFrozenMoney, accountTradeValObj.getBalance(), MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 测试并发冻结资金
     */
    @Test
    public void _0104() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        accountTradeEntity.topUpBalance(initBalance);

        // 冻结金额
        double sumOfFrozenMoney = ThreadUtil.asyncTaskProvideDouble(money -> {
            boolean result = accountTradeEntity.frozenFund(money);

            // 执行结果正常则直接返回
            if (result) {
                return true;
            }

            // 执行结果不正常则判断是否是余额扣完了，扣完了则属于正常情况直接返回
            AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
            if (accountTradeValObj.getBalance() < money) {
                return true;
            }
            // 如果余额小于 0 则抛出异常
            else if (accountTradeValObj.getBalance() < 0) {
                ExceptionHelper
                    .throwTradeDomainException("账户余额不能小于 0，accountTradeId: [{}]", accountTradeEntity.getId());
            }

            // 其他情况说明需要重试，返回 false
            return false;
        });

        // 判断账户总金额是否正确
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(initBalance, accountTradeValObj.getBalance() + accountTradeValObj.getFrozenFund(),
            MoneyConstants.DOUBLE_DELTA);

        // 判断总的冻结金额是否正确
        Assert.assertEquals(sumOfFrozenMoney, accountTradeValObj.getFrozenFund(), MoneyConstants.DOUBLE_DELTA);
        // 判断账户余额是否正确
        Assert.assertEquals(initBalance - sumOfFrozenMoney, accountTradeValObj.getBalance(),
            MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 测试解冻资金
     */
    @Test
    public void _0105() {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        double initFrozenFund = 8000;
        accountTradeEntity.topUpBalance(initBalance);
        accountTradeEntity.frozenFund(initFrozenFund);

        // 解冻资金
        double sumOfUnfrozenMoney = ThreadUtil.syncTaskProvideDouble(accountTradeEntity::unfrozenFund);

        // 判断解冻的金额是否正确
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(initBalance - initFrozenFund + sumOfUnfrozenMoney, accountTradeValObj.getBalance(),
            MoneyConstants.DOUBLE_DELTA);
        Assert.assertEquals(initFrozenFund - sumOfUnfrozenMoney, accountTradeValObj.getFrozenFund(),
            MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 测试并发解冻资金
     */
    @Test
    public void _0106() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000D;
        double initFrozenFund = 8000;
        accountTradeEntity.topUpBalance(initBalance);
        accountTradeEntity.frozenFund(initFrozenFund);

        // 解冻资金
        double sumOfUnfrozenMoney = ThreadUtil.asyncTaskProvideDouble(money -> {
            boolean result = accountTradeEntity.unfrozenFund(money);
            // 如果成功正常返回
            if (result) {
                return true;
            }

            // 失败则需要判断是不是余额不够扣减了
            AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
            if (accountTradeValObj.getFrozenFund() < money) {
                return true;
            }
            // 如果余额小于 0 则需要报错
            else if (accountTradeValObj.getFrozenFund() < 0) {
                ExceptionHelper
                    .throwTradeDomainException("冻结余额不能小于 0，accountTradeId: [{}]", accountTradeEntity.getId());
            }

            return false;
        });

        // 判断解冻的金额是否正确
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();
        Assert.assertEquals(initBalance - initFrozenFund + sumOfUnfrozenMoney,
            accountTradeValObj.getBalance(), MoneyConstants.DOUBLE_DELTA);
        Assert.assertEquals(initFrozenFund - sumOfUnfrozenMoney, accountTradeValObj.getFrozenFund(),
            MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTradeEntity, null);
    }

    /**
     * 测试单个账户转移冻结资金
     */
    @Test
    public void _0108() {
        // 创建账户
        AccountTradeEntity accountTrade = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        double initFrozenFund = 80000;
        accountTrade.topUpBalance(initBalance);
        accountTrade.frozenFund(initFrozenFund);

        double actuallyBalance = initBalance - initFrozenFund;

        // 测试转账
        testTransferFrozenFund(accountTrade, accountTrade, initFrozenFund, actuallyBalance);
    }

    /**
     * 测试两个账户间转移冻结资金
     */
    @Test
    public void _0109() {
        // 创建账户
        AccountTradeEntity accountTrade1 = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        double initFrozenFund = 80000;
        accountTrade1.topUpBalance(initBalance);
        accountTrade1.frozenFund(initFrozenFund);

        AccountTradeEntity accountTrade2 = accountTradeEntityFactory.postAccountTrade();
        accountTrade2.topUpBalance(initBalance);
        accountTrade2.frozenFund(initFrozenFund);

        double actuallyBalance = initBalance - initFrozenFund;

        // 测试转账
        testTransferFrozenFund(accountTrade1, accountTrade2, initFrozenFund, actuallyBalance);
    }

    /**
     * 测试转账冻结金额
     *
     * @param accountTrade1   账户 1
     * @param accountTrade2   账户 2
     * @param initFrozenFund  初始冻结金额
     * @param actuallyBalance 初始账户余额
     */
    private void testTransferFrozenFund(
        AccountTradeEntity accountTrade1,
        AccountTradeEntity accountTrade2,
        double initFrozenFund,
        double actuallyBalance
    ) {
        // 从账户 1 给账户 2 转冻结资金
        double sumOfTransferMoney = ThreadUtil
            .syncTaskProvideDouble(money -> accountTrade1.transferFrozenFund(accountTrade2, money));

        // 判断账户 1 减少的冻结金额是否正确
        AccountTradeValObj accountTradeValObj = accountTrade1.getAccountTradeValObj();
        Assert.assertEquals(initFrozenFund - sumOfTransferMoney, accountTradeValObj.getFrozenFund(),
            MoneyConstants.DOUBLE_DELTA);
        // 判断账户 2 增加的余额是否正确
        AccountTradeValObj accountTradeValObj1 = accountTrade2.getAccountTradeValObj();
        Assert.assertEquals(actuallyBalance + sumOfTransferMoney, accountTradeValObj1.getBalance(),
            MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTrade1, null);
        assertAccount(accountTrade2, null);
    }

    /**
     * 并发给自己转冻结资金
     */
    @Test
    public void _0110() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTrade = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        double initFrozenFund = 80000;
        accountTrade.topUpBalance(initBalance);
        accountTrade.frozenFund(initFrozenFund);

        double actuallyBalance = initBalance - initFrozenFund;

        // 测试转账
        testAsyncTransferFrozenFund(accountTrade, accountTrade, initFrozenFund, actuallyBalance);
    }

    /**
     * 并发在两个账户间转冻结资金
     */
    @Test
    public void _0111() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTrade1 = accountTradeEntityFactory.postAccountTrade();
        double initBalance = 100000;
        double initFrozenFund = 80000;
        accountTrade1.topUpBalance(initBalance);
        accountTrade1.frozenFund(initFrozenFund);

        AccountTradeEntity accountTrade2 = accountTradeEntityFactory.postAccountTrade();
        accountTrade2.topUpBalance(initBalance);
        accountTrade2.frozenFund(initFrozenFund);

        double actuallyBalance = initBalance - initFrozenFund;

        // 测试转账
        testAsyncTransferFrozenFund(accountTrade1, accountTrade2, initFrozenFund, actuallyBalance);
    }

    /**
     * 测试并发转账冻结金额
     *
     * @param accountTrade1   账户 1
     * @param accountTrade2   账户 2
     * @param initFrozenFund  初始冻结金额
     * @param actuallyBalance 初始账户余额
     */
    private void testAsyncTransferFrozenFund(
        AccountTradeEntity accountTrade1,
        AccountTradeEntity accountTrade2,
        double initFrozenFund,
        double actuallyBalance
    ) throws InterruptedException {

        // 从账户 1 给账户 2 转冻结资金
        double sumOfTransferMoney = ThreadUtil.asyncTaskProvideDouble(money -> {
            boolean result = accountTrade1.transferFrozenFund(accountTrade2, money);

            // 如果是余额不够扣减了也正常返回
            AccountTradeValObj accountTradeValObj = accountTrade1.getAccountTradeValObj();
            if (!result && accountTradeValObj.getFrozenFund() < money) {
                return true;
            }

            return result;
        });

        // 判断账户 1 减少的冻结金额是否正确
        AccountTradeValObj accountTradeValObj = accountTrade1.getAccountTradeValObj();
        Assert.assertEquals(initFrozenFund - sumOfTransferMoney, accountTradeValObj.getFrozenFund(),
            MoneyConstants.DOUBLE_DELTA);
        // 判断账户 2 增加的余额是否正确
        AccountTradeValObj accountTradeValObj1 = accountTrade2.getAccountTradeValObj();
        Assert.assertEquals(actuallyBalance + sumOfTransferMoney, accountTradeValObj1.getBalance(),
            MoneyConstants.DOUBLE_DELTA);

        assertAccount(accountTrade1, null);
        assertAccount(accountTrade2, null);
    }

    /**
     * 增加标的数量
     */
    @Test
    public void _0113() {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 将数量充值到账户
        long sumOfSymbolQuantity = ThreadUtil
            .syncTaskProvideLong(quantity -> accountTradeEntity.topUpSymbol(symbolId, quantity));

        // 判断总数量是否相等
        Assert.assertEquals(sumOfSymbolQuantity,
            accountTradeEntity.getHoldPositionQuantity(symbolId).getQuantity().longValue());

        assertAccount(accountTradeEntity, Collections.singletonList(symbolId));
    }

    /**
     * 并发增加标的数量，这个测试类由于开始时并发创建持仓记录所以会报错，属于正常情况
     */
    @Test
    public void _0114() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 将数量充值到账户
        long sumOfSymbolQuantity = ThreadUtil
            .asyncTaskProvideLong(quantity -> accountTradeEntity.topUpSymbol(symbolId, quantity));

        // 判断总数量是否相等
        Assert.assertEquals(sumOfSymbolQuantity,
            accountTradeEntity.getHoldPositionQuantity(symbolId).getQuantity().longValue());

        assertAccount(accountTradeEntity, Collections.singletonList(symbolId));
    }

    /**
     * 冻结标的数量
     */
    @Test
    public void _0115() {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        accountTradeEntity.topUpSymbol(symbolId, initQuantity);

        // 冻结账户数量
        long sumOfFrozenQuantity = ThreadUtil
            .syncTaskProvideLong(quantity -> accountTradeEntity.frozenSymbolQuantity(symbolId, quantity));

        // 判断总数量是否相等
        HoldPositionValObj holdPositionQuantity = accountTradeEntity.getHoldPositionQuantity(symbolId);
        Assert.assertEquals(initQuantity, holdPositionQuantity.getQuantity() +
            holdPositionQuantity.getFrozenQuantity());

        // 判断冻结的数量是否正常
        Assert.assertEquals(sumOfFrozenQuantity, holdPositionQuantity.getFrozenQuantity().longValue());

        assertAccount(accountTradeEntity, Collections.singletonList(symbolId));
    }

    /**
     * 并发冻结标的数量
     */
    @Test
    public void _0116() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        accountTradeEntity.topUpSymbol(symbolId, initQuantity);

        // 并发冻结
        long sumQuantity = ThreadUtil
            .asyncTaskProvideLong(quantity -> accountTradeEntity.frozenSymbolQuantity(symbolId, quantity));

        // 判断总数量是否相等
        HoldPositionValObj holdPositionQuantity = accountTradeEntity.getHoldPositionQuantity(symbolId);
        Assert.assertEquals(initQuantity, holdPositionQuantity.getQuantity() +
            holdPositionQuantity.getFrozenQuantity());

        // 判断冻结的数量是否正常
        Assert.assertEquals(sumQuantity, holdPositionQuantity.getFrozenQuantity().longValue());

        assertAccount(accountTradeEntity, Collections.singletonList(symbolId));
    }

}
