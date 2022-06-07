package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/02 10:07
 */
@Data
public class WithDelTimePO extends WithDelPO {

    /**
     * 删除时间，在删除的时候会自动将删除时间填充上，做删除唯一时，如果带上
     * deleteTime 作为唯一键会有一个缺陷，就是一秒只能删除一条数据，如果对删除
     * 频率不敏感的场景无所谓，如果敏感的场景需要在引入一个 key，在删除时将其置为 id
     */
    private LocalDateTime deleteTime;
}
