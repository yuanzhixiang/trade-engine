package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.math.BigDecimal;

import org.springframework.transaction.support.TransactionTemplate;

import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountTradeParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountTradePO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialHoldPositionPO;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.exception.OptimisticLockerException;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.BaseEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountTradeValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.HoldPositionValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountTradeRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.HoldPositionRepository;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.TradeDomainException;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 交易账户实体，感觉这是一个聚合根，不是实体
 *
 * 对单个账户内的情况乐观锁控制一下如果操作失败则失败了，但是对于两个账户间的情况那么需要更新两次才能将数据更新正确 这个时候如果第一个账户更新成功，第二个账户更新失败，那么需要事务回滚，否则数据不正确
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 13:52
 */
@Slf4j
public class AccountTradeEntity extends BaseEntity {

    private AccountTradeRepository accountTradeRepository;

    private HoldPositionRepository holdPositionRepository;

    private TransactionTemplate transactionTemplate;

    public AccountTradeEntity(Long id) {
        super(id);
        accountTradeRepository = ApplicationContextHelper.getBean(AccountTradeRepository.class);
        holdPositionRepository = ApplicationContextHelper.getBean(HoldPositionRepository.class);
        transactionTemplate = ApplicationContextHelper.getBean(TransactionTemplate.class);
    }

    /**
     * 删除交易账户
     *
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteAccountTradeEntity() {
        return accountTradeRepository.deleteById(getId());
    }

    /**
     * 给账户充值
     *
     * @param fund 金额
     * @return true 充值成功/ false 充值失败
     */
    public boolean topUpBalance(double fund) {
        AccountTradeValObj currentValObj = getAccountTradeValObj();

        boolean result = false;

        // 更新余额，当充值金额大于 0 才可以正常充值
        if (fund >= 0) {
            double newBalance = BigDecimal.valueOf(currentValObj.getBalance()).add(BigDecimal.valueOf(fund))
                .doubleValue();
            result = accountTradeRepository.putBalanceAndFrozenFund(id, newBalance, null,
                currentValObj.getVersion());
        }

        return result;
    }

    /**
     * 给另一个交易账户转钱
     *
     * @param accountTradeEntity 接受资金的账户
     * @return true 转账成功/ false 转账失败
     * @throws OptimisticLockerException 当转账失败时可能抛出乐观锁异常
     */
    public boolean transferFrozenFund(AccountTradeEntity accountTradeEntity, Double fund) {
        AccountTradeValObj currentValObj = getAccountTradeValObj();

        // 如果是我自己给自己转账，那么解冻资金即可
        if (id.equals(accountTradeEntity.getId())) {
            return doUnfrozenFund(fund, currentValObj);
        }

        boolean result = false;

        // 如果是两个账户之间转账则要分别保存两个账户
        // 账户冻结的资金需要大于等于转账的资金才可以转账
        if (currentValObj.getFrozenFund() >= fund) {
            result = transactionTemplate.execute(status -> {
                AccountTradeValObj otherValObj = accountTradeEntity.getAccountTradeValObj();
                boolean putResult;

                // 计算当前账户的冻结金额
                double currentAccountFrozenFund = BigDecimal.valueOf(currentValObj.getFrozenFund())
                    .subtract(BigDecimal.valueOf(fund)).doubleValue();

                // 计算增加余额账户的新余额
                double otherAccountBalance = BigDecimal.valueOf(otherValObj.getBalance()).add(BigDecimal.valueOf(fund))
                    .doubleValue();

                // 进行转账
                putResult = accountTradeRepository.putBalanceAndFrozenFund(id, currentValObj.getBalance(),
                    currentAccountFrozenFund, currentValObj.getVersion()) &&
                    accountTradeRepository.putBalanceAndFrozenFund(accountTradeEntity.id,
                        otherAccountBalance, otherValObj.getFrozenFund(), otherValObj.getVersion());

                // 更新失败，直接抛乐观锁异常
                if (!putResult) {
                    throw new OptimisticLockerException();
                }

                return true;
            });
        }
        return result;
    }

    /**
     * 解冻资金
     *
     * @param fund          准备解冻的资金
     * @param currentValObj 当前账户值对象
     * @return true 解冻成功/ false 解冻失败
     */
    private boolean doUnfrozenFund(Double fund, AccountTradeValObj currentValObj) {

        boolean result = false;

        // 账户可解冻的资金需要大于等于要解冻的资金才可以解冻
        if (currentValObj.getFrozenFund() >= fund) {
            double newBalance = BigDecimal.valueOf(currentValObj.getBalance()).add(BigDecimal.valueOf(fund))
                .doubleValue();
            double newFrozenFund = BigDecimal.valueOf(currentValObj.getFrozenFund())
                .subtract(BigDecimal.valueOf(fund))
                .doubleValue();
            result = accountTradeRepository
                .putBalanceAndFrozenFund(id, newBalance, newFrozenFund, currentValObj.getVersion());
        }

        return result;
    }

    /**
     * 冻结资金
     *
     * @param fund 冻结的资金量
     * @return true 冻结成功/ false 冻结失败
     */
    public boolean frozenFund(double fund) {
        boolean result = false;

        AccountTradeValObj currentValObj = getAccountTradeValObj();

        // 扣减余额，当余额大于扣减的金额则正常扣减
        if (currentValObj.getBalance() >= fund) {
            double newBalance = BigDecimal.valueOf(currentValObj.getBalance()).subtract(BigDecimal.valueOf(fund))
                .doubleValue();
            double newFrozenFund = BigDecimal.valueOf(currentValObj.getFrozenFund()).add(BigDecimal.valueOf(fund))
                .doubleValue();
            result = accountTradeRepository
                .putBalanceAndFrozenFund(id, newBalance, newFrozenFund, currentValObj.getVersion());
        }

        return result;
    }

    /**
     * 解冻资金
     *
     * @param fund 解冻的资金量
     * @return true 解冻成功/ false 解冻失败
     */
    public boolean unfrozenFund(double fund) {
        AccountTradeValObj currentValObj = getAccountTradeValObj();

        // 解冻资金
        return doUnfrozenFund(fund, currentValObj);
    }

    /**
     * 增加账户对指定标的的持有数量
     *
     * @param symbolId 标的 id
     * @param quantity 增加数量
     * @return true 增加成功/ false 增加失败
     */
    public boolean topUpSymbol(Long symbolId, Long quantity) {
        FinancialHoldPositionPO holdPosition = holdPositionRepository
            .getHoldPositionOptional(id, symbolId).orElseGet(() -> {
                // 如果没有查到持仓记录，那么就去创建一条持仓记录
                FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
                financialHoldPositionPO.setAccountTradeId(id);
                financialHoldPositionPO.setSymbolId(symbolId);
                financialHoldPositionPO.setQuantity(0L);
                financialHoldPositionPO.setFrozenQuantity(0L);
                financialHoldPositionPO.setVersion(0L);

                holdPositionRepository.postHoldPosition(financialHoldPositionPO);
                return financialHoldPositionPO;
            });

        // 增加持仓数量
        return holdPositionRepository.putQuantity(holdPosition.getId(), holdPosition.getQuantity() + quantity,
            null, holdPosition.getVersion());
    }

    /**
     * 查询指定标的的持仓数量
     *
     * @param symbolId 标的 id
     * @return 持仓数量
     */
    public HoldPositionValObj getHoldPositionQuantity(Long symbolId) {
        FinancialHoldPositionPO holdPosition = holdPositionRepository.getHoldPositionOptional(id, symbolId).orElse(null);
        HoldPositionValObj holdPositionValObj = new HoldPositionValObj();
        holdPositionValObj.setSymbolId(symbolId);

        if (holdPosition == null) {
            holdPositionValObj.setQuantity(0L);
            holdPositionValObj.setFrozenQuantity(0L);
        } else {
            holdPositionValObj.setQuantity(holdPosition.getQuantity());
            holdPositionValObj.setFrozenQuantity(holdPosition.getFrozenQuantity());
        }
        return holdPositionValObj;
    }

    /**
     * 标的数量转账
     *
     * @param accountTradeEntity 接收转账的账户
     * @param symbolId           标的 id
     * @param quantity           转账数量
     * @return true 转账成功/ false 转账失败
     * @throws OptimisticLockerException 当转账失败时可能抛出乐观锁异常
     */
    public boolean transferSymbolQuantity(AccountTradeEntity accountTradeEntity, Long symbolId, Long quantity) {
        // 当前账户持仓记录
        FinancialHoldPositionPO currentHoldPosition = holdPositionRepository.getHoldPositionOptional(id, symbolId)
            .orElseThrow(() -> ExceptionHelper
                .createException(TradeDomainException.class, "转账交易账户不应该没有该标的的记录，accountTradeId: [{}], symbolId: [{}]",
                    id, symbolId));

        // 余额不够转账直接返回
        if (currentHoldPosition.getFrozenQuantity() < quantity) {
            return false;
        }

        // 如果为倒手的情况则保存一次即可
        if (id.equals(accountTradeEntity.id)) {
            return holdPositionRepository.putQuantity(
                currentHoldPosition.getId(),
                currentHoldPosition.getQuantity() + quantity,
                currentHoldPosition.getFrozenQuantity() - quantity,
                currentHoldPosition.getVersion());
        }

        // 接收转账的持仓记录
        FinancialHoldPositionPO otherHoldPosition = holdPositionRepository
            .getHoldPositionOptional(accountTradeEntity.id, symbolId).orElseGet(() -> {
                // 如果没有查到持仓记录，那么就去创建一条持仓记录
                FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
                financialHoldPositionPO.setAccountTradeId(accountTradeEntity.id);
                financialHoldPositionPO.setSymbolId(symbolId);
                financialHoldPositionPO.setQuantity(0L);
                financialHoldPositionPO.setFrozenQuantity(0L);
                financialHoldPositionPO.setVersion(0L);

                holdPositionRepository.postHoldPosition(financialHoldPositionPO);
                return financialHoldPositionPO;
            });

        // 分别更新两条记录的数量
        currentHoldPosition.setFrozenQuantity(currentHoldPosition.getFrozenQuantity() - quantity);
        otherHoldPosition.setQuantity(otherHoldPosition.getQuantity() + quantity);

        // 将记录更新到数据库中
        return transactionTemplate.execute(status -> {
            boolean putResult = holdPositionRepository.putQuantity(currentHoldPosition.getId(),
                currentHoldPosition.getQuantity(), currentHoldPosition.getFrozenQuantity(),
                currentHoldPosition.getVersion()) &&

                holdPositionRepository.putQuantity(otherHoldPosition.getId(), otherHoldPosition.getQuantity(),
                    otherHoldPosition.getFrozenQuantity(), otherHoldPosition.getVersion());

            // 更新失败，直接抛乐观锁异常
            if (!putResult) {
                throw new OptimisticLockerException();
            }

            return true;
        });
    }

    /**
     * 冻结标的物数量
     *
     * @param symbolId 标的 id
     * @param quantity 冻结数量
     * @return true 冻结成功/ false 冻结失败
     */
    public boolean frozenSymbolQuantity(Long symbolId, Long quantity) {
        FinancialHoldPositionPO financialHoldPositionPO = holdPositionRepository.getHoldPosition(id, symbolId);

        boolean result = false;

        // 持有的数量大于要冻结的数量才可以冻结
        if (financialHoldPositionPO.getQuantity() >= quantity) {
            result = holdPositionRepository.putQuantity(financialHoldPositionPO.getId(),
                financialHoldPositionPO.getQuantity() - quantity,
                    financialHoldPositionPO.getFrozenQuantity() + quantity,
                financialHoldPositionPO.getVersion());
        }

        return result;
    }

    /**
     * 解冻被冻结的标的数量
     *
     * @param symbolId 标的 id
     * @param quantity 解冻数量
     * @return true 解冻成功/ false 解冻失败
     */
    public boolean unfrozenSymbolQuantity(Long symbolId, Long quantity) {
        FinancialHoldPositionPO financialHoldPositionPO = holdPositionRepository.getHoldPositionOptional(id, symbolId)
            .orElseThrow(() -> ExceptionHelper
                .createException(TradeDomainException.class,
                    "解冻交易账户不应该没有该标的的记录，accountTradeId: [{}], symbolId: [{}]",
                    id, symbolId));

        boolean result = false;

        // 被冻结的数量大于要解冻的数量才可以解冻
        if (financialHoldPositionPO.getFrozenQuantity() >= quantity) {
            result = holdPositionRepository
                .putQuantity(financialHoldPositionPO.getId(), financialHoldPositionPO.getQuantity() + quantity,
                    financialHoldPositionPO.getFrozenQuantity() - quantity, financialHoldPositionPO.getVersion());
        }

        return result;
    }

    /**
     * 获取交易账户实体的值
     */
    public AccountTradeValObj getAccountTradeValObj() {
        AccountTradeParam accountTradeParam = new AccountTradeParam();
        accountTradeParam.setId(id);
        FinancialAccountTradePO financialAccountTradePO = accountTradeRepository.getAccountTradeOptional(accountTradeParam).get();
        AccountTradeValObj accountTradeValObj = new AccountTradeValObj();
        accountTradeValObj.setAccountId(financialAccountTradePO.getAccountId());
        accountTradeValObj.setBalance(financialAccountTradePO.getBalance());
        accountTradeValObj.setFrozenFund(financialAccountTradePO.getFrozenFund());
        accountTradeValObj.setVersion(financialAccountTradePO.getVersion());
        return accountTradeValObj;
    }
}
