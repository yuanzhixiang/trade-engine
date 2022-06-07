package com.yuanzhixiang.trade.engine.trade.web;

import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.trade.application.TradeSymbolAggregateApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.TradeSymbolAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.factory.TradeSymbolAggregateFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.tradesymbol.TradeSymbolValObj;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.exception.TradeSymbolException;
import com.yuanzhixiang.trade.engine.trade.utils.SymbolUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeSymbolGetListRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeSymbolGetPageRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.TradeSymbolPostRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.TradeSymbolVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 交易标服务
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 18:15
 */
@Slf4j
@RestController
@RequestMapping("/api/financial/tradeSymbol")
public class TradeSymbolController {

    @Autowired
    private TradeSymbolAggregateFactory tradeSymbolAggregateFactory;

    @Autowired
    private TradeSymbolAggregateApplication tradeSymbolAggregateApplication;

    /**
     * 新建标的
     */
    @PostMapping
    public Response<Void> postTradeSymbol(@Valid @RequestBody TradeSymbolPostRequest request) {
        TradeSymbolValObj tradeSymbolValObj = new TradeSymbolValObj();
        tradeSymbolValObj.setSymbol(SymbolUtil.generateSymbol());
        tradeSymbolAggregateFactory.postTradeSymbol(tradeSymbolValObj);
        return Response.success();
    }

    /**
     * 查询交易标的列表
     *
     * @return 交易标的列表
     */
    @GetMapping("/getTradeSymbolList")
    public Response<List<TradeSymbolVO>> getTradeSymbolList(TradeSymbolGetListRequest request) {
        List<TradeSymbolVO> result = tradeSymbolAggregateApplication.getTradeSymbolList().stream()
                .filter(aggregate -> filterByEngineStatus(aggregate, request.getEngineStatus()))
                .map(this::aggregateToVo).collect(Collectors.toList());
        return Response.success(result);
    }

    /**
     * 分页查询交易标的列表
     *
     * @param request 分页查询请求
     * @return 分页查询结果
     */
    @GetMapping("/getTradeSymbolPage")
    public Response<PageResult<TradeSymbolVO>> getTradeSymbolPage(TradeSymbolGetPageRequest request) {
        PageResult<TradeSymbolAggregate> tradeSymbolPage = tradeSymbolAggregateApplication.getTradeSymbolPage(request);
        return Response.success(PageResult.getPageResult(tradeSymbolPage, tradeSymbolAggregates ->
                tradeSymbolAggregates.stream()
                        .filter(aggregate -> filterByEngineStatus(aggregate, request.getEngineStatus()))
                        .map(this::aggregateToVo).collect(Collectors.toList())));
    }

    /**
     * 删除交易标的
     *
     * @param symbolId 聚合根 id
     * @return 删除指定交易标的
     */
    @DeleteMapping
    public Response<Void> deleteTradeSymbol(@RequestParam("symbolId") Long symbolId) {
        TradeSymbolAggregate tradeSymbolAggregate = getTradeSymbolAggregate(symbolId);
        tradeSymbolAggregate.deleteTradeSymbol();
        return Response.success();
    }

    /**
     * 开启交易引擎
     *
     * @param symbolId 聚合根 id
     */
    @PutMapping("/startTradeEngine")
    public Response<Void> startTradeEngine(@RequestParam("symbolId") Long symbolId) {
        TradeSymbolAggregate tradeSymbolAggregate = getTradeSymbolAggregate(symbolId);
        tradeSymbolAggregate.startTradeEngine();
        return Response.success();
    }

    /**
     * 关闭交易引擎
     *
     * @param symbolId 交易引擎
     */
    @PutMapping("/closeTradeEngine")
    public Response<Void> closeTradeEngine(@RequestParam("symbolId") Long symbolId) {
        TradeSymbolAggregate tradeSymbolAggregate = getTradeSymbolAggregate(symbolId);
        tradeSymbolAggregate.closeTradeEngine();
        return Response.success();
    }

    private TradeSymbolAggregate getTradeSymbolAggregate(Long symbolId) {
        Optional<TradeSymbolAggregate> tradeSymbol = tradeSymbolAggregateFactory.getTradeSymbol(symbolId);

        return tradeSymbol.orElseThrow(() ->
                ExceptionHelper.createException(TradeSymbolException.class, "该聚合根不存在，id: [{{}]", symbolId));
    }

    private boolean filterByEngineStatus(TradeSymbolAggregate aggregate, String engineStatus) {
        // 如果没传状态则不按照状态过滤
        if (StrUtil.isBlank(engineStatus)) {
            return true;
        }
        // 如果传递了状态则过滤出相同的状态
        return engineStatus.equals(aggregate.getEngineStatus().name());
    }

    /**
     * 将聚合根映射成视图对象
     *
     * @param aggregate 聚合根
     * @return 视图对象
     */
    private TradeSymbolVO aggregateToVo(TradeSymbolAggregate aggregate) {
        TradeSymbolVO tradeSymbolVO = new TradeSymbolVO();
        tradeSymbolVO.setId(aggregate.getId());
        tradeSymbolVO.setSymbol(aggregate.getSymbol());
        tradeSymbolVO.setEngineStatus(aggregate.getEngineStatus().name());
        return tradeSymbolVO;
    }
}
