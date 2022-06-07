package com.yuanzhixiang.trade.engine.v1.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.order.OrderActionEnum;
import com.yuanzhixiang.trade.engine.v1.model.bo.OrderBO;
import com.yuanzhixiang.trade.engine.v1.model.vo.CanCancelOrderVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderCancelVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderCreateVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.OrderVO;
import com.yuanzhixiang.trade.engine.v1.model.vo.TradeOrderVO;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;

import com.yuanzhixiang.trade.engine.v1.converter.OrderWebConverter;
import com.yuanzhixiang.trade.engine.v1.service.OrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单系统
 *
 * @author zhixiang.yuan
 * @since 2021/02/14 20:43:15
 */
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderWebConverter orderWebConverter;

    @Autowired
    private EngineController engineController;

    /**
     * 接收用户的委托单
     */
    @PostMapping("/createOrder")
    public Response<String> createOrder(@Valid @RequestBody OrderCreateVO orderCreateVO) {

        OrderBO orderBO = new OrderBO();
        orderBO.setUserId(orderCreateVO.getUserId());
        orderBO.setSymbol(orderCreateVO.getSymbol());
        orderBO.setAmount(orderCreateVO.getAmount());
        orderBO.setPrice(orderCreateVO.getPrice());
        orderBO.setSide(orderCreateVO.getSide());
        orderBO.setType(orderCreateVO.getType());
        orderBO.setAction(OrderActionEnum.CREATE.name());
        orderService.insertCreateOrder(orderBO);

        TradeOrderVO tradeOrderVO = new TradeOrderVO();
        tradeOrderVO.setId(orderBO.getId());
        tradeOrderVO.setSymbol(orderBO.getSymbol());
        tradeOrderVO.setAmount(orderBO.getAmount());
        tradeOrderVO.setPrice(orderBO.getPrice());
        tradeOrderVO.setAction(orderBO.getAction());
        tradeOrderVO.setSide(orderBO.getSide());
        tradeOrderVO.setType(orderBO.getType());

        engineController.handleOrder(tradeOrderVO);

        return Response.success("处理成功");
    }

    /**
     * 查询用户可撤单
     *
     * @param userId 用户 id
     * @return 可撤单
     */
    @GetMapping("/selectCanCancelOrder")
    public Response<List<CanCancelOrderVO>> selectCanCancelOrder(
        @RequestParam("userId") Long userId) {
        List<CanCancelOrderVO> canCancelOrderVOList = orderService.selectCanCancelOrder(userId);
        return Response.success(canCancelOrderVOList);
    }

    /**
     * 取消委托单
     */
    @PostMapping("cancelOrder")
    public Response<String> cancelOrder(
        @RequestBody OrderCancelVO orderCancelVO
    ) {
        OrderBO orderBO = new OrderBO();
        orderBO.setUserId(orderCancelVO.getUserId());
        orderBO.setCancelOrderId(orderCancelVO.getCancelOrderId());
        orderBO.setSymbol(orderCancelVO.getSymbol());
        orderBO.setAction(OrderActionEnum.CANCEL.name());
        orderService.insertCancelOrder(orderBO);

        TradeOrderVO tradeOrderVO = new TradeOrderVO();
        tradeOrderVO.setCancelOrderId(orderBO.getCancelOrderId());
        tradeOrderVO.setAction(orderBO.getAction());
        tradeOrderVO.setSymbol(orderBO.getSymbol());

        engineController.handleOrder(tradeOrderVO);
        return Response.success("处理成功");
    }

    /**
     * 查询所有委托单
     *
     * @param userId 用户 id
     */
    @GetMapping("selectAllOrderByUserId")
    public Response<List<OrderVO>> selectAllOrderByUserId(@RequestParam("userId") Long userId) {
        return Response
            .success(orderWebConverter.boToVo(orderService.selectOrderListByUserId(userId)));
    }

}
