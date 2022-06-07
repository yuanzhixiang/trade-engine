package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.sqlinject;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po.WithDelTimePO;

/**
 * 重新实现 deleteById 方法
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/02 15:19
 */
public class DeleteByIdMethod extends DeleteById {

    private static final String DELETE_TIME = "delete_time";

    @Override
    protected String sqlLogicSet(TableInfo table) {
        // 这里只会在启动系统的时候进来一次
        String sql = "SET " + table.getLogicDeleteSql(false, false);
        if (WithDelTimePO.class.isAssignableFrom(table.getEntityType())) {
            sql += "," + DELETE_TIME + "=NOW()";
        }
        return sql;
    }

    @Override
    public String getMethod(SqlMethod sqlMethod) {
        return "deleteById";
    }
}
