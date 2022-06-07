package com.yuanzhixiang.trade.engine.trade.application.task;

import com.yuanzhixiang.trade.engine.trade.application.AccountLoginEntityApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 系统启动后执行的任务
 *
 * @author zhixiang.yuan
 * @since 2021/06/22 02:07:15
 */
@Component
public class SystemStartAfterTask implements ApplicationRunner {

    @Autowired
    private AccountLoginEntityApplication accountLoginEntityApplication;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化 session
        accountLoginEntityApplication.initLocalSession();
    }
}
