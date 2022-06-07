package com.yuanzhixiang.trade.engine.v1.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.v1.converter.WalletWebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.WalletBO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import com.yuanzhixiang.trade.engine.v1.dao.WalletMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 17:35:55
 */
@Service
public class WalletService {

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletWebConverter walletWebConverter;

    public WalletBO selectByUserId(Long userId) {
        LambdaQueryWrapper<WalletPO> walletWrapper = new LambdaQueryWrapper<>();
        walletWrapper.eq(WalletPO::getUserId, userId);
        WalletPO walletPO = walletMapper.selectOne(walletWrapper);
        return walletWebConverter.poToBo(walletPO);
    }
}
