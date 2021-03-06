package com.yuanzhixiang.trade.engine.v1.service.liquidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lmax.disruptor.EventHandler;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderSideEnum;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.annotation.OptimisticLockerWhile;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.kit.OptimisticLockerKit;
import com.yuanzhixiang.trade.engine.trade.web.log.annotation.CatchException;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core.VoucherEvent;
import com.yuanzhixiang.trade.engine.v1.converter.TradeVoucherConverter;
import com.yuanzhixiang.trade.engine.v1.dao.HoldPositionMapper;
import com.yuanzhixiang.trade.engine.v1.dao.OrderMapper;
import com.yuanzhixiang.trade.engine.v1.dao.SymbolMapper;
import com.yuanzhixiang.trade.engine.v1.dao.TradeVoucherMapper;
import com.yuanzhixiang.trade.engine.v1.dao.WalletMapper;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.CancelVoucherValObj;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.TradeVoucherValObj;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;
import com.yuanzhixiang.trade.engine.v1.model.po.OrderPO;
import com.yuanzhixiang.trade.engine.v1.model.po.SymbolPO;
import com.yuanzhixiang.trade.engine.v1.model.po.TradeVoucherPO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import com.yuanzhixiang.trade.engine.v1.service.HoldPositionService;
import com.yuanzhixiang.trade.engine.v1.service.OrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 14:43:16
 */
@Slf4j
@Service
public class LiquidateService implements EventHandler<VoucherEvent> {

    @Autowired
    private TradeVoucherMapper tradeVoucherMapper;

    @Autowired
    private TradeVoucherConverter tradeVoucherConverter;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private HoldPositionService holdPositionService;

    @Autowired
    private HoldPositionMapper holdPositionMapper;

    @Autowired
    private SymbolMapper symbolMapper;

    @Override
    @CatchException(throwException = false)
    @OptimisticLockerWhile
    @Transactional(rollbackFor = RuntimeException.class)
    public void onEvent(VoucherEvent event, long sequence, boolean endOfBatch) {
        TradeVoucherValObj tradeVoucherValObj = event.getTradeVoucherValObj();
        if (tradeVoucherValObj != null) {
            // ?????????????????????
            doTradeVoucher(tradeVoucherValObj);
        }

        CancelVoucherValObj cancelVoucherValObj = event.getCancelVoucherValObj();
        if (cancelVoucherValObj != null) {
            // ?????????????????????
            doCancelVoucher(cancelVoucherValObj);
        }
    }

    private void doCancelVoucher(CancelVoucherValObj cancelVoucherValObj) {
        // ?????????????????????
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            OrderPO orderPO = orderService.selectOrderById(cancelVoucherValObj.getOrderId());
            orderPO.setCancelAmount(cancelVoucherValObj.getRemainQuantity());

            if (OrderSideEnum.isBuyOrder(orderPO.getSide())) {
                doCancelBuyVoucher(cancelVoucherValObj, orderPO);
            } else {
                doCancelSellVoucher(cancelVoucherValObj, orderPO);
            }

            return orderMapper.updateById(orderPO) == 1;
        });
    }

    private void doCancelSellVoucher(CancelVoucherValObj cancelVoucherValObj, OrderPO orderPO) {
        // ??????????????????????????????
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            HoldPositionPO holdPositionPO =
                holdPositionService.selectHoldPosition(orderPO.getUserId(), orderPO.getSymbol());

            holdPositionPO.setFrozenSellAmount(
                holdPositionPO.getFrozenSellAmount() - cancelVoucherValObj.getRemainQuantity());
            holdPositionPO.setPositionAmount(
                holdPositionPO.getPositionAmount() + cancelVoucherValObj.getRemainQuantity());
            return holdPositionMapper.updateById(holdPositionPO) == 1;
        });
    }

    private void doCancelBuyVoucher(CancelVoucherValObj cancelVoucherValObj, OrderPO orderPO) {
        // ??????????????????????????????
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            LambdaQueryWrapper<WalletPO> walletWrapper = new LambdaQueryWrapper<>();
            walletWrapper.eq(WalletPO::getUserId, orderPO.getUserId());
            WalletPO walletPO = walletMapper.selectOne(walletWrapper);

            double cancelFund = cancelVoucherValObj.getRemainQuantity() * orderPO.getPrice();
            walletPO.setFrozenFund(walletPO.getFrozenFund() - cancelFund);
            walletPO.setBalance(walletPO.getBalance() + cancelFund);
            return walletMapper.updateById(walletPO) == 1;
        });

    }

    private void doTradeVoucher(TradeVoucherValObj tradeVoucherValObj) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // ??????????????????
            TradeVoucherPO tradeVoucherPO = tradeVoucherConverter.boToPo(tradeVoucherValObj);
            tradeVoucherMapper.insert(tradeVoucherPO);

            // ?????????????????????????????????
            OrderPO makerOrder = orderService.selectOrderById(tradeVoucherPO.getMakerId());
            OrderPO takeOrder = orderService.selectOrderById(tradeVoucherPO.getTakeId());

            // ??????
            doTransferFund(tradeVoucherPO, makerOrder, takeOrder);

            // ????????????
            doTransferPosition(tradeVoucherPO, makerOrder, takeOrder);

            // ?????????????????????????????????
            makerOrder.setTradeAmount(makerOrder.getTradeAmount() + tradeVoucherPO.getAmount());
            takeOrder.setTradeAmount(takeOrder.getTradeAmount() + tradeVoucherPO.getAmount());

            // ???????????????
            updateSymbolLatestPrice(tradeVoucherValObj);

            return orderMapper.updateById(makerOrder) == 1 && orderMapper.updateById(takeOrder) == 1;
        });

    }

    private void updateSymbolLatestPrice(TradeVoucherValObj tradeVoucherValObj) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // ?????????????????????
            LambdaQueryWrapper<SymbolPO> symbolWrapper = new LambdaQueryWrapper<>();
            symbolWrapper.eq(SymbolPO::getSymbol, tradeVoucherValObj.getSymbolId());
            SymbolPO symbolPO = symbolMapper.selectOne(symbolWrapper);

            // ?????????????????????
            // todo ?????????????????????
//            symbolPO.setSymbol(tradeVoucherBO.getSymbolId());
            symbolPO.setLatestPrice(tradeVoucherValObj.getPrice());
            return symbolMapper.update(symbolPO, symbolWrapper) == 1;
        });

    }

    /**
     * ????????????
     *
     * @param tradeVoucherPO ????????????
     * @param makerOrder     ??????????????????
     * @param takeOrder      ??????????????????
     */
    private void doTransferPosition(TradeVoucherPO tradeVoucherPO, OrderPO makerOrder,
        OrderPO takeOrder) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // ????????????
            HoldPositionPO makerHoldPositionPO = queryHoldPosition(makerOrder);

            HoldPositionPO takeHoldPositionPO;

            if (makerOrder.getUserId().equals(takeOrder.getUserId())) {
                // ????????????????????????????????????????????????
                takeHoldPositionPO = makerHoldPositionPO;
            } else {
                takeHoldPositionPO = queryHoldPosition(takeOrder);
            }

            HoldPositionPO buyHoldPositionPO;
            HoldPositionPO sellHoldPositionPO;
            if (OrderSideEnum.isSellOrder(tradeVoucherPO.getTakerSide())) {
                buyHoldPositionPO = makerHoldPositionPO;
                sellHoldPositionPO = takeHoldPositionPO;
            } else {
                buyHoldPositionPO = takeHoldPositionPO;
                sellHoldPositionPO = makerHoldPositionPO;
            }

//            if (sellHoldPositionPO.getId().equals(buyHoldPositionPO.getId())) {
//                // ?????????????????????????????????
//            }
//            // ??????????????????????????????
//            else if (buyHoldPositionPO.getId() != null) {
//                // ????????????????????????????????????????????????????????????????????? + ????????????????????????
//                double totalFund = buyHoldPositionPO.getCostPrice() *
//                    (buyHoldPositionPO.getPositionAmount() + buyHoldPositionPO.getFrozenSellAmount())
//                    + tradeVoucherPO.getPrice() * tradeVoucherPO.getAmount();
//                // ??????????????????????????????
//                long totalAmount = buyHoldPositionPO.getPositionAmount() + tradeVoucherPO.getAmount();
//                double costPrice =
//                    new BigDecimal(totalFund)
//                        .divide(new BigDecimal(totalAmount), 2, RoundingMode.HALF_UP)
//                        .doubleValue();
//                buyHoldPositionPO.setCostPrice(costPrice);
//            } else {
//                // ?????????????????????????????????????????????????????????????????????
//                buyHoldPositionPO.setCostPrice(tradeVoucherPO.getPrice());
//            }

            // todo ???????????????????????????????????????????????? 1
            buyHoldPositionPO.setCostPrice(1D);
            sellHoldPositionPO.setCostPrice(1D);

            // ????????????
            sellHoldPositionPO
                .setFrozenSellAmount(
                    sellHoldPositionPO.getFrozenSellAmount() - tradeVoucherPO.getAmount());
            buyHoldPositionPO
                .setPositionAmount(buyHoldPositionPO.getPositionAmount() + tradeVoucherPO.getAmount());

            // ????????????????????????????????????
            if (sellHoldPositionPO.getId().equals(buyHoldPositionPO.getId())) {
                return holdPositionMapper.updateById(sellHoldPositionPO) == 1;
            }

            if (buyHoldPositionPO.getId() != null) {
                // ??????????????????????????????
                if (holdPositionMapper.updateById(buyHoldPositionPO) == 0) {
                    return false;
                }
            } else {
                // ????????????????????????????????????????????????????????????????????????????????????????????????
                holdPositionMapper.insert(buyHoldPositionPO);
            }

            if (sellHoldPositionPO.getPositionAmount() + sellHoldPositionPO.getFrozenSellAmount() == 0) {
                // ???????????????????????????????????????????????????
                // ??????????????????????????????????????????????????????????????????
                holdPositionMapper.deleteById(sellHoldPositionPO.getId());
            } else {
                // ??????????????????????????????
                if (holdPositionMapper.updateById(sellHoldPositionPO) == 0) {
                    return false;
                }
            }

            // ?????????????????????????????????
            return true;
        });

    }

    private HoldPositionPO queryHoldPosition(OrderPO orderPO) {
        HoldPositionPO makerHoldPositionPO =
            holdPositionService.selectHoldPosition(orderPO.getUserId(), orderPO.getSymbol());
        if (makerHoldPositionPO == null) {
            makerHoldPositionPO = new HoldPositionPO();
            makerHoldPositionPO.setUserId(orderPO.getUserId());
            makerHoldPositionPO.setSymbol(orderPO.getSymbol());
            makerHoldPositionPO.setPositionAmount(0L);
        }
        return makerHoldPositionPO;
    }

    /**
     * ??????
     *
     * @param tradeVoucherPO ????????????
     * @param makerOrder     ??????????????????
     * @param takeOrder      ??????????????????
     */
    private void doTransferFund(TradeVoucherPO tradeVoucherPO, OrderPO makerOrder,
        OrderPO takeOrder) {
        OptimisticLockerKit.doOptimisticLockExecute(() -> {
            // ????????????
            LambdaQueryWrapper<WalletPO> makerWalletWrapper = new LambdaQueryWrapper<>();
            makerWalletWrapper.eq(WalletPO::getUserId, makerOrder.getUserId());
            WalletPO makerWallet = walletMapper.selectOne(makerWalletWrapper);
            WalletPO takeWallet;

            if (makerOrder.getUserId().equals(takeOrder.getUserId())) {
                // ?????????????????????????????????????????????????????????
                takeWallet = makerWallet;
            } else {
                LambdaQueryWrapper<WalletPO> takeWalletWrapper = new LambdaQueryWrapper<>();
                takeWalletWrapper.eq(WalletPO::getUserId, takeOrder.getUserId());
                takeWallet = walletMapper.selectOne(takeWalletWrapper);
            }

            // ????????????
            double fund = tradeVoucherPO.getPrice() * tradeVoucherPO.getAmount();

            // ??????????????????
            WalletPO buyWallet;
            WalletPO sellWallet;
            if (OrderSideEnum.isSellOrder(tradeVoucherPO.getTakerSide())) {
                buyWallet = makerWallet;
                sellWallet = takeWallet;
            } else {
                buyWallet = takeWallet;
                sellWallet = makerWallet;
            }

            // ??????
            buyWallet.setFrozenFund(buyWallet.getFrozenFund() - fund);
            sellWallet.setBalance(sellWallet.getBalance() + fund);

            // ??????????????????????????????????????????????????????
            if (buyWallet.getUserId().equals(sellWallet.getUserId())) {
                return walletMapper.updateById(buyWallet) == 1;
            }
            // ???????????????????????????????????????????????????????????????
            else {
                return walletMapper.updateById(buyWallet) == 1 && walletMapper.updateById(sellWallet) == 1;
            }
        });
    }
}
