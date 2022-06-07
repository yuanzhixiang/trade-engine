package com.yuanzhixiang.trade.engine.trade.web;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.trade.application.MarketEntityApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.market.MarketKLineValObj;
import com.yuanzhixiang.trade.engine.trade.web.model.request.KMarketGetRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.KMarketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 行情服务
 *
 * @author zhixiang.yuan
 * @since 2021/06/20 21:11:50
 */
@RestController
@RequestMapping("/api/financial/market")
public class MarketController {

    @Autowired
    private MarketEntityApplication marketEntityApplication;

    /**
     * 查询 k 线行情
     *
     * @param kMarketGetRequest k 线行情请求
     * @return k 线行情
     */
    @GetMapping("/getKMarket")
    public Response<List<KMarketVO>> getKMarket(KMarketGetRequest kMarketGetRequest) {
        List<KMarketVO> collect = marketEntityApplication.getMarketKLineList(kMarketGetRequest)
                .stream().map(this::valObj2Vo).collect(Collectors.toList());

        return Response.success(collect);
    }

    private KMarketVO valObj2Vo(MarketKLineValObj marketKLineValObj) {
        KMarketVO kMarketVO = new KMarketVO();
        kMarketVO.setTime(marketKLineValObj.getTime());
        kMarketVO.setOpen(marketKLineValObj.getOpen());
        kMarketVO.setClose(marketKLineValObj.getClose());
        kMarketVO.setLow(marketKLineValObj.getLow());
        kMarketVO.setHigh(marketKLineValObj.getHigh());
        kMarketVO.setVolume(marketKLineValObj.getVolume());
        return kMarketVO;
    }
}
