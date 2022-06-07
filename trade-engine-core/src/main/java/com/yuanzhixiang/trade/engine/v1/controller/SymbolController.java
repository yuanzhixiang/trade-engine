package com.yuanzhixiang.trade.engine.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.converter.SymbolWebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.SymbolBO;
import com.yuanzhixiang.trade.engine.v1.model.vo.SymbolVO;
import com.yuanzhixiang.trade.engine.v1.service.SymbolService;
import lombok.extern.slf4j.Slf4j;

/**
 * 标的服务
 *
 * @author zhixiang.yuan
 * @since 2021/02/16 16:50:49
 */
@Slf4j
public class SymbolController {

    @Autowired
    private SymbolService symbolService;

    @Autowired
    private SymbolWebConverter symbolWebConverter;

    /**
     * 查找标的信息
     *
     * @param symbol 标的编码
     * @return 标的信息
     */
    @GetMapping
    public Response<SymbolVO> selectSymbol(@RequestParam("symbol") String symbol) {
        SymbolBO symbolBO = symbolService.selectBySymbol(symbol);
        return Response.success(symbolWebConverter.boToVo(symbolBO));
    }
}
