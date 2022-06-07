package com.yuanzhixiang.trade.engine.trade.domain.model.entity.engine.core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine.EngineOrderValObj;

/**
 * 委托账本，账本主要干两个事情，一个是对委托单进行定序，另一个作用是存储暂未交易的委托单
 *
 * @author ZhiXiang Yuan
 * @date 2021/02/09 19:24
 */
public class TradeOrderBook {

    /**
     * 管理买入委托单的委托账本，价格从大到小排序
     */
    private List<Deque<EngineOrderValObj>> buyOrderList = new ArrayList<>();
    /**
     * 管理卖出委托单的委托账本，价格从小到大排序
     */
    private List<Deque<EngineOrderValObj>> sellOrderList = new ArrayList<>();

    /**
     * 订单视图
     */
    private Map<Long, EngineOrderValObj> orderBOMap = new HashMap<>();

    public Map<Long, EngineOrderValObj> getOrderBOMap() {
        return orderBOMap;
    }

    public List<Deque<EngineOrderValObj>> getBuyOrderList() {
        return buyOrderList;
    }

    public List<Deque<EngineOrderValObj>> getSellOrderList() {
        return sellOrderList;
    }

    /**
     * 向委托账本中放入买入委托单
     *
     * @param engineOrderValObj 委托单
     */
    public void insertBuyOrder(EngineOrderValObj engineOrderValObj) {
        insertOrder(engineOrderValObj, buyOrderList, ((val1, val2) -> val1 < val2));
    }

    /**
     * 委托单重新放回账本
     *
     * @param engineOrderValObj 委托单
     */
    public void backInsertBuyOrder(EngineOrderValObj engineOrderValObj) {
        backInsertOrder(engineOrderValObj, buyOrderList, ((val1, val2) -> val1 < val2));
    }

    /**
     * 向委托账本中放入卖出委托单
     *
     * @param engineOrderValObj 委托单
     */
    public void insertSellOrder(EngineOrderValObj engineOrderValObj) {
        insertOrder(engineOrderValObj, sellOrderList, ((val1, val2) -> val1 > val2));
    }

    /**
     * 委托单重新放回账本
     *
     * @param engineOrderValObj 委托单
     */
    public void backInsertSellOrder(EngineOrderValObj engineOrderValObj) {
        backInsertOrder(engineOrderValObj, sellOrderList, ((val1, val2) -> val1 > val2));
    }

    private void backInsertOrder(
        EngineOrderValObj engineOrderValObj, List<Deque<EngineOrderValObj>> orderList, BiDoublePredicate predicate
    ) {
        if (engineOrderValObj == null) {
            return;
        }
        orderBOMap.put(engineOrderValObj.getId(), engineOrderValObj);
        int index;
        for (index = 0; index < orderList.size(); index++) {
            Deque<EngineOrderValObj> tradeOrderValObjList = orderList.get(index);
            EngineOrderValObj firstElement = tradeOrderValObjList.peek();

            // 当前的委托单价格与该队列相同则将其放入队列
            if (firstElement.getPrice().equals(engineOrderValObj.getPrice())) {
                tradeOrderValObjList.addFirst(engineOrderValObj);
                return;
            }
            // 当不满足条件时跳出循环
            else if (predicate.test(firstElement.getPrice(), engineOrderValObj.getPrice())) {
                break;
            }
        }

        // 在指定的索引前面增加价格队列
        LinkedList<EngineOrderValObj> tradeOrderValObjList = new LinkedList<>();
        tradeOrderValObjList.add(engineOrderValObj);
        orderList.add(index, tradeOrderValObjList);
    }

    private void insertOrder(
        EngineOrderValObj engineOrderValObj, List<Deque<EngineOrderValObj>> orderList, BiDoublePredicate predicate
    ) {
        if (engineOrderValObj == null) {
            return;
        }
        orderBOMap.put(engineOrderValObj.getId(), engineOrderValObj);
        int index;
        for (index = 0; index < orderList.size(); index++) {
            Deque<EngineOrderValObj> tradeOrderValObjList = orderList.get(index);
            EngineOrderValObj firstElement = tradeOrderValObjList.peek();

            // 当前的委托单价格与该队列相同则将其放入队列
            if (firstElement.getPrice().equals(engineOrderValObj.getPrice())) {
                tradeOrderValObjList.addLast(engineOrderValObj);
                return;
            }
            // 当不满足条件时跳出循环
            else if (predicate.test(firstElement.getPrice(), engineOrderValObj.getPrice())) {
                break;
            }
        }

        // 在指定的索引前面增加价格队列
        LinkedList<EngineOrderValObj> engineOrderValObjList = new LinkedList<>();
        engineOrderValObjList.add(engineOrderValObj);
        orderList.add(index, engineOrderValObjList);
    }
}

@FunctionalInterface
interface BiDoublePredicate {
    boolean test(double val1, double val2);
}
