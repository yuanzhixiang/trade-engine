package com.yuanzhixiang.trade.engine.trade.domain.model.aggregate;

import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import lombok.Getter;

/**
 * @author ZhiXiang Yuan
 * @date 2021/05/29 20:48
 */
public class BaseAggregate {

    public BaseAggregate(Long id) {
        if (id == null) {
            ExceptionHelper.throwTradeDomainException("id 不能为 null");
        }
        this.id = id;
    }

    /**
     * 唯一标识
     */
    @Getter
    private Long id;

    protected void setId(Long id) {
        this.id = id;
    }
}
