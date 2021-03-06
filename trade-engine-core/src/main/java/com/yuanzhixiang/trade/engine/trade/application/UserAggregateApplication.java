package com.yuanzhixiang.trade.engine.trade.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yuanzhixiang.trade.engine.trade.application.converter.UserApplicationConverter;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.repository.HoldPositionRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.UserRepository;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.HoldPositionParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.TradeOrderParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialHoldPositionPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialOrderPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialUserPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.tradeorder.TradeOrderRepository;
import com.yuanzhixiang.trade.engine.trade.web.model.request.UserGetPageRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.AccountSymbolVO;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.CanCancelOrderVO;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.user.UserFactory;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:27
 */
@Component
public class UserAggregateApplication {

    @Autowired
    private UserApplicationConverter userApplicationConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private HoldPositionRepository holdPositionRepository;

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    @Autowired
    private TradeOrderRepository tradeOrderRepository;

    /**
     * ??????????????????
     *
     * @param request ????????????
     * @return ????????????????????????
     */
    public PageResult<UserAggregate> getUserPage(UserGetPageRequest request) {
        UserParam userParam = userApplicationConverter.userGetPageRequestToParam(request);
        PageResult<FinancialUserPO> userPage = userRepository.getUserPage(userParam);
        return PageResult.getPageResult(userPage,
            financialUserPOS -> financialUserPOS.stream().map(financialUserPO ->
                new UserAggregate(financialUserPO.getId())).collect(Collectors.toList())
        );
    }

    /**
     * ????????????????????????????????????
     *
     * @param userId ?????? id
     * @return ????????????
     */
    public PageResult<AccountSymbolVO> getAccountSymbolPage(Long userId) {
        UserAggregate user = userFactory.getUser(userId);
        AccountTradeEntity accountTradeEntity = user.getAccountTradeEntity();
        HoldPositionParam holdPositionParam = new HoldPositionParam();
        holdPositionParam.setAccountTradeId(accountTradeEntity.getId());
        PageResult<FinancialHoldPositionPO> holdPositionPage = holdPositionRepository
            .getHoldPositionPage(holdPositionParam);
        return PageResult.getPageResult(holdPositionPage,
            holdPositionPOS -> holdPositionPOS.stream().map(holdPositionPO -> {
                AccountSymbolVO accountSymbolVO = new AccountSymbolVO();
                accountSymbolVO.setSymbolId(holdPositionPO.getSymbolId());
                accountSymbolVO.setQuantity(holdPositionPO.getQuantity());
                accountSymbolVO.setFrozenQuantity(holdPositionPO.getFrozenQuantity());

                Optional<TradeSymbolAggregate> tradeSymbol = tradeSymbolAggregateFactory
                    .getTradeSymbol(holdPositionPO.getSymbolId());
                tradeSymbol.ifPresent(tradeSymbolAggregate -> {
                    accountSymbolVO.setSymbol(tradeSymbolAggregate.getSymbol());
                });
                return accountSymbolVO;
            }).collect(Collectors.toList())
        );
    }

    /**
     * ?????????????????????
     *
     * @param userId ?????? id
     * @return ???????????????
     */
    public List<CanCancelOrderVO> getCanCancelOrderList(Long userId) {
        UserAggregate user = userFactory.getUser(userId);
        AccountTradeEntity accountTradeEntity = user.getAccountTradeEntity();

        TradeOrderParam tradeOrderParam = new TradeOrderParam();
        tradeOrderParam.setAccountTradeId(accountTradeEntity.getId());
        List<FinancialOrderPO> canCancelOrder = tradeOrderRepository.getCanCancelOrder(tradeOrderParam);
        return canCancelOrder.stream().map(financialOrderPO -> {
            CanCancelOrderVO canCancelOrderVO = new CanCancelOrderVO();
            canCancelOrderVO.setId(financialOrderPO.getId());
            canCancelOrderVO.setQuantity(financialOrderPO.getQuantity());
            canCancelOrderVO.setPrice(financialOrderPO.getPrice());
            canCancelOrderVO.setSide(financialOrderPO.getSide());
            canCancelOrderVO.setCompletedQuantity(financialOrderPO.getCompletedQuantity());
            canCancelOrderVO.setCanceledQuantity(financialOrderPO.getCanceledQuantity());
            canCancelOrderVO.setGmtCreate(financialOrderPO.getGmtCreate());

            canCancelOrderVO.setSymbolId(financialOrderPO.getSymbolId());

            Optional<TradeSymbolAggregate> tradeSymbol = tradeSymbolAggregateFactory
                .getTradeSymbol(financialOrderPO.getSymbolId());
            tradeSymbol.ifPresent(tradeSymbolAggregate -> canCancelOrderVO.setSymbol(tradeSymbolAggregate.getSymbol()));
            return canCancelOrderVO;
        }).collect(Collectors.toList());
    }
}
