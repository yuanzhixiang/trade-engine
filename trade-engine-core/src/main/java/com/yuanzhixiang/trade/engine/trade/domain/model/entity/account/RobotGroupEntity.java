package com.yuanzhixiang.trade.engine.trade.domain.model.entity.account;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.yuanzhixiang.trade.engine.trade.domain.model.factory.user.UserFactory;
import com.yuanzhixiang.trade.engine.trade.utils.ApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 机器人群组管理
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/25 15:58
 */
@Slf4j
public class RobotGroupEntity {

    private RobotGroupEntity(Long symbolId) {
        this.symbolId = symbolId;
        userFactory = ApplicationContextHelper.getBean(UserFactory.class);
    }

    private Long symbolId;

    private UserFactory userFactory;

    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private static HashMap<Long, RobotGroupEntity> instanceMap = new HashMap<>();

    public static synchronized RobotGroupEntity getInstance(Long symbolId) {
        RobotGroupEntity robotGroupEntity = instanceMap.get(symbolId);
        if (robotGroupEntity == null) {
            robotGroupEntity = new RobotGroupEntity(symbolId);
            instanceMap.put(symbolId, robotGroupEntity);
        }

        return robotGroupEntity;
    }

    /**
     * 开启交易任务
     */
    public synchronized void startTradeTask() {
        if (scheduledThreadPoolExecutor != null) {
            return;
        }

        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        List<RobotEntity> robotEntityList = userFactory.getRobotEntityList(symbolId);
        // 开始交易
        robotEntityList.forEach(robotEntity -> scheduledThreadPoolExecutor.scheduleWithFixedDelay(() -> {
            try {
                robotEntity.tradeTask();
            } catch (Exception e) {
                log.error("", e);
            }
        }, 1, 1, TimeUnit.SECONDS));
    }

    /**
     * 关闭交易任务
     */
    public synchronized void stopTradeTask() {
        if (scheduledThreadPoolExecutor == null) {
            return;
        }

        scheduledThreadPoolExecutor.shutdown();
        scheduledThreadPoolExecutor = null;
    }
}
