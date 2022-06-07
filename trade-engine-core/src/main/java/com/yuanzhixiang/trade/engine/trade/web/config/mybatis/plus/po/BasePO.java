package com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import com.yuanzhixiang.trade.engine.trade.web.config.mybatis.plus.AutoFillMetaObjectHandler;

import lombok.Data;

/**
 * @author ZhiXiang Yuan
 * @date 2020/10/17 17:55
 */
@Data
public class BasePO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 记录创建时间，该字段在插入时会在程序中生成时间戳进行赋值，配置见 {@link AutoFillMetaObjectHandler}
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;
    /**
     * 记录最后一次修改时间，该字段在插入和更新时会在程序中生成时间戳进行赋值，配置见 {@link AutoFillMetaObjectHandler}
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;
    /**
     * 乐观锁
     */
    @Version
    private Long version;
}
