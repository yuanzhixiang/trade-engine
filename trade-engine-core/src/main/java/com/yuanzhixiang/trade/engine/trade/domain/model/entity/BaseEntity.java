package com.yuanzhixiang.trade.engine.trade.domain.model.entity;

import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/29 20:52
 */
@Data
public class BaseEntity {

    /**
     * 唯一标识
     */
    protected Long id;

    public BaseEntity(Long id) {
        if (id == null) {
            ExceptionHelper.throwTradeDomainException("id 不能为 null");
        }
        this.id = id;
    }
}
