package com.yuanzhixiang.trade.engine.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.converter.WalletWebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.WalletBO;
import com.yuanzhixiang.trade.engine.v1.model.vo.WalletVO;
import com.yuanzhixiang.trade.engine.v1.service.WalletService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 11:23:24
 */
@Slf4j
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletWebConverter walletWebConverter;

    /**
     * 查找用户余额
     *
     * @param userId 用户主键
     * @return 钱包信息
     */
    @GetMapping
    public Response<WalletVO> selectByUserId(
        @RequestParam("userId") Long userId
    ) {
        WalletBO walletBO = walletService.selectByUserId(userId);
        return Response.success(walletWebConverter.boToVo(walletBO));
    }
}
