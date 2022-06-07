package com.yuanzhixiang.trade.engine.trade.web;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.TradeOrderValObj;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.TradeSymbolException;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContext;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContextUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeOrderPostRequest;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * 委托单服务
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 23:20
 */
@Slf4j
@RestController
@RequestMapping("/api/financial/tradeOrder")
public class TradeOrderController {

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    /**
     * 提交委托单
     *
     * @param request 委托单信息
     */
    @PostMapping
    public Response<String> submitTradeOrder(@Valid @RequestBody TradeOrderPostRequest request) {
        // 查询出交易标的聚合根
        TradeSymbolAggregate tradeSymbolAggregate = getTradeSymbolAggregate(request.getSymbolId());

        if (!tradeSymbolAggregate.engineIsOpen()) {
            ExceptionHelper.throwSystemException("引擎未开启");
        }

        // 获取业务上下文
        BusinessContext businessContext = BusinessContextUtil.get();
        UserAggregate user = businessContext.getUser();
        AccountTradeEntity accountTradeEntity = user.getAccountTradeEntity();

        // 创建委托单值对象
        TradeOrderValObj tradeOrderValObj = new TradeOrderValObj();
        tradeOrderValObj.setAccountTradeId(accountTradeEntity.getId());
        tradeOrderValObj.setSymbolId(request.getSymbolId());
        tradeOrderValObj.setQuantity(request.getAmount());
        tradeOrderValObj.setPrice(request.getPrice());
        tradeOrderValObj.setSide(request.getSide());
        tradeOrderValObj.setType(request.getType());
        tradeOrderValObj.setCompletedQuantity(0L);
        tradeOrderValObj.setCanceledQuantity(0L);

        // 提交交易委托单
        tradeSymbolAggregate.submitTradeOrder(tradeOrderValObj);
        return Response.success("委托单提交成功");
    }

    /**
     * 撤销委托单
     *
     * @param symbolId      标的 id
     * @param cancelOrderId 需要取消的委托单 id
     */
    @PutMapping("/cancelTradeOrder")
    public Response<String> submitCancelOrder(
        @RequestParam("symbolId") Long symbolId,
        @RequestParam("cancelOrderId") Long cancelOrderId
    ) {
        TradeSymbolAggregate tradeSymbolAggregate = getTradeSymbolAggregate(symbolId);
        tradeSymbolAggregate.submitCancelOrder(cancelOrderId);
        return Response.success("提交成功");
    }

    private TradeSymbolAggregate getTradeSymbolAggregate(Long symbolId) {
        Optional<TradeSymbolAggregate> tradeSymbol = tradeSymbolAggregateFactory.getTradeSymbol(symbolId);

        return tradeSymbol.orElseThrow(() ->
            ExceptionHelper.createException(TradeSymbolException.class, "该聚合根不存在，id: [{{}]", symbolId));
    }
}
