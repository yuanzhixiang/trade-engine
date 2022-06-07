package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lmax.disruptor.EventHandler;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.AccountTradeParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialAccountTradePO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialHoldPositionPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder.TradeOrderRepository;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerWhile;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit.OptimisticLockerKit;
import com.yuanzhixiang.trade.engine.trade.web.log.annotation.CatchException;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.VoucherEvent;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;
import com.yuanzhixiang.trade.engine.trade.domain.repository.AccountTradeRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.HoldPositionRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.TradeVoucherRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 交易凭证处理器，用处理交易后需要完成的资金与证券转账，该实体为无状态实体
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/31 13:59
 */
@Slf4j
@Component
public class TradeVoucherHandlerEntity implements EventHandler<VoucherEvent> {

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    @Autowired
    private AccountTradeRepository accountTradeRepository;

    @Autowired
    private HoldPositionRepository holdPositionRepository;

    @Autowired
    private TradeVoucherRepository tradeVoucherRepository;

    @Override
    @CatchException(throwException = false)
    @OptimisticLockerWhile
    @Transactional(rollbackFor = RuntimeException.class)
    public void onEvent(VoucherEvent event, long sequence, boolean endOfBatch) throws Exception {
        TradeVoucherValObj tradeVoucherValObj = event.getTradeVoucherValObj();
        if (tradeVoucherValObj != null) {
            // 处理成交的单子
            doTradeVoucher(tradeVoucherValObj);
        }

        CancelVoucherValObj cancelVoucherValObj = event.getCancelVoucherValObj();
        if (cancelVoucherValObj != null) {
            // 处理撤销的单子
            doCanceledVoucher(cancelVoucherValObj);
        }
    }

    /**
     * 处理交易凭证
     *
     * @param tradeVoucherValObj 交易凭证值对象
     */
    private void doTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 交易凭证入库
            tradeVoucherRepository.postTradeVoucher(tradeVoucherValObj);

            // 查询双方委托单
            FinancialOrderPO makerOrderPO = tradeOrderRepository.getTradeOrder(tradeVoucherValObj.getMakerId());
            FinancialOrderPO takeOrderPO = tradeOrderRepository.getTradeOrder(tradeVoucherValObj.getTakeId());

            // 查询双方交易账户
            AccountTradeParam accountTradeParam = new AccountTradeParam();
            accountTradeParam.setId(makerOrderPO.getAccountTradeId());
            FinancialAccountTradePO makerAccountTradePO = accountTradeRepository.getAccountTrade(accountTradeParam);
            FinancialAccountTradePO takeAccountTradePO;

            // 确定挂单与吃单账户
            if (makerOrderPO.getAccountTradeId().equals(takeOrderPO.getAccountTradeId())) {
                // 如果是自己给自己转账则将 take 账户给为 maker
                takeAccountTradePO = makerAccountTradePO;
            } else {
                accountTradeParam = new AccountTradeParam();
                accountTradeParam.setId(takeOrderPO.getAccountTradeId());
                takeAccountTradePO = accountTradeRepository.getAccountTrade(accountTradeParam);
            }

            // 确定买方与卖方账户
            FinancialAccountTradePO buyAccountTradePO;
            FinancialAccountTradePO sellAccountTradePO;
            FinancialOrderPO buyOrderPO;
            if (OrderSideEnum.isSellOrder(tradeVoucherValObj.getTakerSide())) {
                buyAccountTradePO = makerAccountTradePO;
                sellAccountTradePO = takeAccountTradePO;
                buyOrderPO = makerOrderPO;
            } else {
                buyAccountTradePO = takeAccountTradePO;
                sellAccountTradePO = makerAccountTradePO;
                buyOrderPO = takeOrderPO;
            }

            // 转账
            doTransferFund(tradeVoucherValObj, buyOrderPO, buyAccountTradePO, sellAccountTradePO);

            // 转移头寸
            doTransferPosition(tradeVoucherValObj, buyAccountTradePO, sellAccountTradePO);

            // 更新委托单中的成交数量
            return tradeOrderRepository.putCompletedQuantity(makerOrderPO.getId(),
                makerOrderPO.getCompletedQuantity() + tradeVoucherValObj.getQuantity(), makerOrderPO.getVersion())
                && tradeOrderRepository.putCompletedQuantity(takeOrderPO.getId(),
                takeOrderPO.getCompletedQuantity() + tradeVoucherValObj.getQuantity(), takeOrderPO.getVersion());
        });
    }

    /**
     * 转账
     *
     * @param tradeVoucherValObj 交易凭证
     * @param buyAccountTradePO  买方账户实体
     * @param sellAccountTradePO 卖方账户实体
     */
    private void doTransferFund(
        TradeVoucherValObj tradeVoucherValObj,
        FinancialOrderPO buyOrderPO,
        FinancialAccountTradePO buyAccountTradePO,
        FinancialAccountTradePO sellAccountTradePO
    ) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // 确定转账资金
            double fund = BigDecimal.valueOf(tradeVoucherValObj.getPrice())
                .multiply(BigDecimal.valueOf(tradeVoucherValObj.getQuantity())).doubleValue();

            // 如果买入的价格小于挂单的价格，则需要将未交易的金额解冻放回去
            double unfrozenFund = 0;
            if (tradeVoucherValObj.getPrice() < buyOrderPO.getPrice()) {
                unfrozenFund = BigDecimal.valueOf(buyOrderPO.getPrice())
                    .subtract(BigDecimal.valueOf(tradeVoucherValObj.getPrice()))
                    .multiply(BigDecimal.valueOf(tradeVoucherValObj.getQuantity())).doubleValue();
            }

            // 如果是自己给自己转账，那么则将冻结资金转到余额即可
            if (buyAccountTradePO.getId().equals(sellAccountTradePO.getId())
                && buyAccountTradePO.getFrozenFund() >= fund) {
                double newBalance = BigDecimal.valueOf(buyAccountTradePO.getBalance())
                    .add(BigDecimal.valueOf(fund))
                    .add(BigDecimal.valueOf(unfrozenFund))
                    .doubleValue();
                double newFrozenFund = BigDecimal.valueOf(buyAccountTradePO.getFrozenFund())
                    .subtract(BigDecimal.valueOf(fund))
                    .subtract(BigDecimal.valueOf(unfrozenFund))
                    .doubleValue();
                return accountTradeRepository
                    .putBalanceAndFrozenFund(buyAccountTradePO.getId(), newBalance, newFrozenFund,
                        buyAccountTradePO.getVersion());
            }

            // 不是自己给自己转账则表示是两个账户之间转账
            if (buyAccountTradePO.getFrozenFund() >= fund) {

                // 计算买方账户的冻结金额
                double buyAccountBalance = BigDecimal.valueOf(buyAccountTradePO.getBalance())
                    .add(BigDecimal.valueOf(unfrozenFund))
                    .doubleValue();
                double buyAccountFrozenFund = BigDecimal.valueOf(buyAccountTradePO.getFrozenFund())
                    .subtract(BigDecimal.valueOf(fund))
                    .subtract(BigDecimal.valueOf(unfrozenFund))
                    .doubleValue();

                // 计算增加余额账户的新余额
                double sellAccountBalance = BigDecimal.valueOf(sellAccountTradePO.getBalance())
                    .add(BigDecimal.valueOf(fund)).doubleValue();

                // 进行转账
                return accountTradeRepository
                    .putBalanceAndFrozenFund(buyAccountTradePO.getId(), buyAccountBalance,
                        buyAccountFrozenFund, buyAccountTradePO.getVersion()) &&
                    accountTradeRepository.putBalanceAndFrozenFund(sellAccountTradePO.getId(),
                        sellAccountBalance, sellAccountTradePO.getFrozenFund(), sellAccountTradePO.getVersion());
            }

            // 转账失败
            return false;
        });
    }

    /**
     * 转移头寸
     *
     * @param tradeVoucherValObj 交易凭证
     * @param buyAccountTradePO  买方账户实体
     * @param sellAccountTradePO 卖方账户实体
     */
    private void doTransferPosition(
        TradeVoucherValObj tradeVoucherValObj,
        FinancialAccountTradePO buyAccountTradePO,
        FinancialAccountTradePO sellAccountTradePO
    ) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            Long symbolId = tradeVoucherValObj.getSymbolId();
            Long quantity = tradeVoucherValObj.getQuantity();

            FinancialHoldPositionPO buyHoldPositionPO = holdPositionRepository
                .getHoldPosition(buyAccountTradePO.getId(), symbolId);

            // 买的人如果还没有持仓则创建持仓
            if (buyHoldPositionPO == null) {
                // 如果没有查到持仓记录，那么就去创建一条持仓记录
                FinancialHoldPositionPO financialHoldPositionPO = new FinancialHoldPositionPO();
                financialHoldPositionPO.setAccountTradeId(buyAccountTradePO.getId());
                financialHoldPositionPO.setSymbolId(symbolId);
                financialHoldPositionPO.setQuantity(0L);
                financialHoldPositionPO.setFrozenQuantity(0L);
                financialHoldPositionPO.setVersion(0L);

                holdPositionRepository.postHoldPosition(financialHoldPositionPO);
                buyHoldPositionPO = financialHoldPositionPO;
            }

            // 如果为倒手的情况则保存一次即可
            if (buyAccountTradePO.getId().equals(sellAccountTradePO.getId())) {
                return holdPositionRepository.putQuantity(
                    buyHoldPositionPO.getId(),
                    buyHoldPositionPO.getQuantity() + quantity,
                    buyHoldPositionPO.getFrozenQuantity() - quantity,
                    buyHoldPositionPO.getVersion());
            }

            // 如果不是倒手则需要往另一个账户中转持仓
            FinancialHoldPositionPO sellHoldPositionPO = holdPositionRepository
                .getHoldPosition(sellAccountTradePO.getId(), symbolId);

            // 分别更新两条记录的数量
            buyHoldPositionPO.setQuantity(buyHoldPositionPO.getQuantity() + quantity);
            sellHoldPositionPO.setFrozenQuantity(sellHoldPositionPO.getFrozenQuantity() - quantity);

            return holdPositionRepository.putQuantity(buyHoldPositionPO.getId(),
                buyHoldPositionPO.getQuantity(), buyHoldPositionPO.getFrozenQuantity(),
                buyHoldPositionPO.getVersion()) &&

                holdPositionRepository.putQuantity(sellHoldPositionPO.getId(), sellHoldPositionPO.getQuantity(),
                    sellHoldPositionPO.getFrozenQuantity(), sellHoldPositionPO.getVersion());
        });
    }

    /**
     * 处理撤单
     *
     * @param cancelVoucherValObj 撤单交易凭证
     */
    private void doCanceledVoucher(CancelVoucherValObj cancelVoucherValObj) {
        // 更新撤单的数量
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            FinancialOrderPO tradeOrderPO = tradeOrderRepository.getTradeOrder(cancelVoucherValObj.getOrderId());

            AccountTradeParam accountTradeParam = new AccountTradeParam();
            accountTradeParam.setId(tradeOrderPO.getAccountTradeId());
            FinancialAccountTradePO makerAccountTradePO = accountTradeRepository.getAccountTrade(accountTradeParam);

            if (OrderSideEnum.isBuyOrder(tradeOrderPO.getSide())) {
                doCanceledBuyVoucher(makerAccountTradePO, tradeOrderPO, cancelVoucherValObj);
            } else {
                doCanceledSellVoucher(makerAccountTradePO, tradeOrderPO, cancelVoucherValObj);
            }

            return tradeOrderRepository
                .putCanceledQuantity(tradeOrderPO.getId(), cancelVoucherValObj.getRemainQuantity());
        });
    }

    /**
     * 撤销买入委托单
     *
     * @param accountTradePO      交易账户实体
     * @param tradeOrderPO        委托单实体
     * @param cancelVoucherValObj 撤销凭证
     */
    private void doCanceledBuyVoucher(
        FinancialAccountTradePO accountTradePO,
        FinancialOrderPO tradeOrderPO,
        CancelVoucherValObj cancelVoucherValObj
    ) {
        // 买单撤单需要解冻资金
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            double unfreezeFund = tradeOrderPO.getPrice() * cancelVoucherValObj.getRemainQuantity();

            double newBalance = BigDecimal.valueOf(accountTradePO.getBalance()).add(BigDecimal.valueOf(unfreezeFund))
                .doubleValue();
            double newFrozenFund = BigDecimal.valueOf(accountTradePO.getFrozenFund())
                .subtract(BigDecimal.valueOf(unfreezeFund))
                .doubleValue();
            return accountTradeRepository
                .putBalanceAndFrozenFund(accountTradePO.getId(), newBalance, newFrozenFund,
                    accountTradePO.getVersion());
        });
    }

    /**
     * 撤销卖出委托单
     *
     * @param accountTradePO      交易账户实体
     * @param tradeOrderPO        委托单实体
     * @param cancelVoucherValObj 撤销凭证
     */
    private void doCanceledSellVoucher(
        FinancialAccountTradePO accountTradePO,
        FinancialOrderPO tradeOrderPO,
        CancelVoucherValObj cancelVoucherValObj
    ) {
        // 卖单撤单需要解冻持仓
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            FinancialHoldPositionPO financialHoldPositionPO = holdPositionRepository
                .getHoldPosition(accountTradePO.getId(), tradeOrderPO.getSymbolId());

            Long remainQuantity = cancelVoucherValObj.getRemainQuantity();
            // 被冻结的数量大于要解冻的数量才可以解冻
            return holdPositionRepository
                .putQuantity(financialHoldPositionPO.getId(), financialHoldPositionPO.getQuantity() + remainQuantity,
                    financialHoldPositionPO.getFrozenQuantity() - remainQuantity, financialHoldPositionPO.getVersion());
        });
    }
}
