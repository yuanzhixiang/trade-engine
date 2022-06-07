package com.yuanzhixiang.trade.engine.v1.service;

import com.yuanzhixiang.trade.engine.v1.dao.UserMapper;
import com.yuanzhixiang.trade.engine.v1.dao.WalletMapper;
import com.yuanzhixiang.trade.engine.v1.model.po.UserPO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhixiang.yuan
 * @since 2021/02/15 16:42:07
 */
@Service
public class UserService {

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public Long initUser() {
        UserPO userPO = new UserPO();
        userMapper.insert(userPO);
        WalletPO walletPO = new WalletPO();
        walletPO.setUserId(userPO.getId());
        // 余额初始化为 100W
        walletPO.setBalance(1000000D);
        walletMapper.insert(walletPO);
        return userPO.getId();
    }
}
