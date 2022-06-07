package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * @author ZhiXiang Yuan
 * @date 2020/11/09 10:09
 */
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    private static final String gmtModified = "gmtModified";
    private static final String gmtCreate = "gmtCreate";
    private static final String deleted = "deleted";

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, gmtCreate, LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, gmtModified, LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, deleted, Boolean.class, Boolean.FALSE);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 这里需要注意，这里面改掉的是传递进来的对象的修改时间
        if (metaObject.hasSetter(gmtModified)) {
            metaObject.setValue(gmtModified, LocalDateTime.now());
        }
    }
}
