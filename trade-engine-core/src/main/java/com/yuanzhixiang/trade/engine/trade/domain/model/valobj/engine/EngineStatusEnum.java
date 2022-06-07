package com.yuanzhixiang.trade.engine.trade.domain.model.valobj.engine;

/**
 * 引擎状态
 *
 * 引擎状态扭转图：NOT_START => RUNNING => CLOSE
 *
 * @author ZhiXiang Yuan
 * @date 2021/05/30 01:08
 */
public enum EngineStatusEnum {
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 关闭
     */
    CLOSE

}
