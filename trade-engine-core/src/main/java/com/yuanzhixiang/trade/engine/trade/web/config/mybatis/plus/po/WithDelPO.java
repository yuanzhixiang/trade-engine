package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.AutoFillMetaObjectHandler;
import lombok.Data;

/**
 * @author zhixiang.yuan
 * @since 2020/11/08 17:20:45
 */
@Data
public class WithDelPO extends BasePO {
    /**
     * 伪删除字段，该字段在插入时会在程序中赋值为 FALSE，配置见 {@link AutoFillMetaObjectHandler}，
     * 逻辑删除等同于真删除，删了代码里就查不出来了，只不过数据还在库里，还可以通过 sql 的方式恢复
     */
    @TableLogic(value = "false", delval = "true")
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
