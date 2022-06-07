package com.yuanzhixiang.trade.engine.v1.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.v1.converter.SymbolWebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.SymbolBO;
import com.yuanzhixiang.trade.engine.v1.model.po.SymbolPO;
import com.yuanzhixiang.trade.engine.v1.dao.SymbolMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 16:51:22
 */
@Service
public class SymbolService {

    @Autowired
    private SymbolMapper symbolMapper;

    @Autowired
    private SymbolWebConverter symbolWebConverter;

    public SymbolBO selectBySymbol(String symbol) {
        LambdaQueryWrapper<SymbolPO> symbolWrapper = new LambdaQueryWrapper<>();
        symbolWrapper.eq(SymbolPO::getSymbol, symbol);
        SymbolPO symbolPO = symbolMapper.selectOne(symbolWrapper);
        return symbolWebConverter.poToBo(symbolPO);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void initLatestPrice(String symbol, Double latestPrice) {
        LambdaQueryWrapper<SymbolPO> symbolWrapper = new LambdaQueryWrapper<>();
        symbolWrapper.eq(SymbolPO::getSymbol, symbol);
        SymbolPO symbolPO = symbolMapper.selectOne(symbolWrapper);
        if (symbolPO != null) {
            symbolPO.setLatestPrice(latestPrice);
            symbolMapper.updateById(symbolPO);
        } else {
            symbolPO = new SymbolPO();
            symbolPO.setSymbol(symbol);
            symbolPO.setLatestPrice(latestPrice);
            symbolMapper.insert(symbolPO);
        }
    }
}
