package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.yuanzhixiang.trade.engine.BaseApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.AccountTradeEntityFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.ThreadUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.util.TradeSymbolUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.HoldPositionValObj;

import lombok.extern.slf4j.Slf4j;

/**
 * 交易账户标的单元测试
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/02 23:01
 */
@Slf4j
public class AccountTradeEntity_Symbol_Test extends BaseApplication {

    @Autowired
    private AccountTradeEntityFactory accountTradeEntityFactory;

    // ----------------- 以下代码已完成重构 -----------------

    /**
     * 解冻标的数量
     */
    @Test
    public void _0117() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        accountTradeEntity.topUpSymbol(symbolId, initQuantity);

        // 冻结数量
        long frozenQuantity = 80000;
        accountTradeEntity.frozenSymbolQuantity(symbolId, frozenQuantity);

        doUnfrozenSymbol(accountTradeEntity, symbolId, syncUnfrozenSymbolTask);
    }

    /**
     * 并发解冻标的
     */
    @Test
    public void _0118() throws InterruptedException {
        // 创建账户
        AccountTradeEntity accountTradeEntity = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        accountTradeEntity.topUpSymbol(symbolId, initQuantity);

        // 冻结数量
        long frozenQuantity = 80000;
        accountTradeEntity.frozenSymbolQuantity(symbolId, frozenQuantity);

        doUnfrozenSymbol(accountTradeEntity, symbolId, asyncUnfrozenSymbolTask);
    }

    /**
     * 同步解冻标的任务
     */
    private final UnfrozenSymbolTask syncUnfrozenSymbolTask = (accountTradeEntity, symbolId) -> ThreadUtil
        .syncTaskProvideLong(quantity -> accountTradeEntity.unfrozenSymbolQuantity(symbolId, quantity));

    /**
     * 异步解冻标的任务
     */
    private final UnfrozenSymbolTask asyncUnfrozenSymbolTask = (accountTradeEntity, symbolId) -> ThreadUtil
        .asyncTaskProvideLong(quantity -> accountTradeEntity.unfrozenSymbolQuantity(symbolId, quantity));

    /**
     * 执行解冻标的
     *
     * @param accountTradeEntity 账户
     * @param symbolId           标的 id
     * @throws InterruptedException 线程打断异常
     */
    private void doUnfrozenSymbol(AccountTradeEntity accountTradeEntity, Long symbolId,
        UnfrozenSymbolTask unfrozenSymbolTask)
        throws InterruptedException {
        HoldPositionValObj initHoldPositionQuantity = accountTradeEntity.getHoldPositionQuantity(symbolId);

        // 并发解冻
        long sumQuantity = unfrozenSymbolTask.run(accountTradeEntity, symbolId);

        // 判断总数量是否相等
        HoldPositionValObj holdPositionQuantity = accountTradeEntity.getHoldPositionQuantity(symbolId);
        Assert.assertEquals(
            initHoldPositionQuantity.getQuantity() + initHoldPositionQuantity.getFrozenQuantity(),
            holdPositionQuantity.getQuantity() +
                holdPositionQuantity.getFrozenQuantity()
        );

        // 判断解冻的数量是否正常
        Assert.assertEquals(initHoldPositionQuantity.getQuantity() + sumQuantity,
            holdPositionQuantity.getQuantity().longValue());
        // 判断解冻的数量是否正常
        Assert.assertEquals(initHoldPositionQuantity.getFrozenQuantity() - sumQuantity,
            holdPositionQuantity.getFrozenQuantity().longValue());

        AccountTradeEntityTest.assertAccount(accountTradeEntity, Collections.singletonList(symbolId));
    }

    /**
     * 解冻标的任务
     */
    private interface UnfrozenSymbolTask {

        /**
         * @param accountTradeEntity 账户
         * @param symbolId           标的 id
         * @return 总解冻数量
         * @throws InterruptedException 线程异常
         */
        long run(AccountTradeEntity accountTradeEntity, Long symbolId) throws InterruptedException;
    }

    /**
     * 测试同账户标的转账的情况
     */
    @Test
    public void _0119() throws InterruptedException {
        // 创建账户
        AccountTradeEntity account = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        long initFrozenQuantity = 80000;
        account.topUpSymbol(symbolId, initQuantity);
        account.frozenSymbolQuantity(symbolId, initFrozenQuantity);

        doTestAsyncTransferSymbolQuantity(account, account, symbolId, syncTransferSymbolQuantityTask);
    }

    /**
     * 测试两个账户间标的转账的情况
     */
    @Test
    public void _0120() throws InterruptedException {
        // 创建账户
        AccountTradeEntity account1 = accountTradeEntityFactory.postAccountTrade();
        AccountTradeEntity account2 = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        long initFrozenQuantity = 80000;
        account1.topUpSymbol(symbolId, initQuantity);
        account1.frozenSymbolQuantity(symbolId, initFrozenQuantity);

        doTestAsyncTransferSymbolQuantity(account1, account2, symbolId, syncTransferSymbolQuantityTask);
    }


    /**
     * 测试标的并发给自己转账的情况
     */
    @Test
    public void _0121() throws InterruptedException {
        // 创建账户
        AccountTradeEntity account = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        long initFrozenQuantity = 80000;
        account.topUpSymbol(symbolId, initQuantity);
        account.frozenSymbolQuantity(symbolId, initFrozenQuantity);

        doTestAsyncTransferSymbolQuantity(account, account, symbolId, asyncTransferSymbolQuantityTask);
    }

    /**
     * 测试并发两个账户间转标的物，由于第二个账户的第一条持仓记录创建的时候会冲突，所以会 Duplicate key 的错误，属于正常情况
     */
    @Test
    public void _0122() throws InterruptedException {
        // 创建账户
        AccountTradeEntity account1 = accountTradeEntityFactory.postAccountTrade();
        AccountTradeEntity account2 = accountTradeEntityFactory.postAccountTrade();
        Long symbolId = TradeSymbolUtil.generateSymbolId();

        // 初始化标的数量
        long initQuantity = 100000;
        long initFrozenQuantity = 80000;
        account1.topUpSymbol(symbolId, initQuantity);
        account1.frozenSymbolQuantity(symbolId, initFrozenQuantity);

        doTestAsyncTransferSymbolQuantity(account1, account2, symbolId, asyncTransferSymbolQuantityTask);
    }

    /**
     * 同步转账实现
     */
    private final TransferSymbolQuantityTask syncTransferSymbolQuantityTask = (_account1, _account2, _symbolId) -> {
        // 账户 1 给账户 2 转账
        return ThreadUtil
            .syncTaskProvideLong(quantity -> _account1.transferSymbolQuantity(_account2, _symbolId, quantity));
    };

    /**
     * 异步转账实现
     */
    private final TransferSymbolQuantityTask asyncTransferSymbolQuantityTask = (_account1, _account2, _symbolId) -> {
        // 账户 1 给账户 2 转账
        return ThreadUtil.asyncTaskProvideLong(quantity -> {
            boolean result = _account1.transferSymbolQuantity(_account2, _symbolId, quantity);
            HoldPositionValObj account_1_holdPosition = _account1.getHoldPositionQuantity(_symbolId);
            // 如果是因为余额不够导致的转账失败返回 true
            if (!result && account_1_holdPosition.getFrozenQuantity() < quantity) {
                return true;
            }
            return result;
        });
    };

    /**
     * 异步账户间转标的物
     *
     * @param account1                   账户 1
     * @param account2                   账户 2
     * @param symbolId                   标的
     * @param transferSymbolQuantityTask 转账任务
     */
    private void doTestAsyncTransferSymbolQuantity(
        AccountTradeEntity account1, AccountTradeEntity account2, Long symbolId,
        TransferSymbolQuantityTask transferSymbolQuantityTask
    ) throws InterruptedException {
        // 获取转账前的数据快照
        HoldPositionValObj account_1_HoldPosition = account1.getHoldPositionQuantity(symbolId);
        HoldPositionValObj account_2_HoldPosition = account2.getHoldPositionQuantity(symbolId);

        // 执行转账任务
        long sumQuantity = transferSymbolQuantityTask.run(account1, account2, symbolId);

        // 取出账户当前的持仓记录
        HoldPositionValObj newAccount_1_HoldPosition = account1.getHoldPositionQuantity(symbolId);
        HoldPositionValObj newAccount_2_HoldPosition = account2.getHoldPositionQuantity(symbolId);

        // 验证账户 1 减少的数量是否正常
        Assert.assertEquals(account_1_HoldPosition.getFrozenQuantity() - sumQuantity,
            newAccount_1_HoldPosition.getFrozenQuantity().longValue());

        // 验证账户 2 增加的数量是否正常
        Assert.assertEquals(account_2_HoldPosition.getQuantity() + sumQuantity,
            newAccount_2_HoldPosition.getQuantity().longValue());

        AccountTradeEntityTest.assertAccount(account1, Collections.singletonList(symbolId));
        AccountTradeEntityTest.assertAccount(account2, Collections.singletonList(symbolId));
    }

    /**
     * 转账任务
     */
    private interface TransferSymbolQuantityTask {

        /**
         * 执行转账任务
         *
         * @param account1 账号 1
         * @param account2 账号 2
         * @param symbolId 标的 id
         * @return 总数量
         */
        long run(AccountTradeEntity account1, AccountTradeEntity account2, Long symbolId) throws InterruptedException;
    }

}
