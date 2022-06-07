package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.databind;

import cn.hutool.core.date.LocalDateTimeUtil;
import java.beans.PropertyEditorSupport;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/22 20:53
 */
public class LocalDateTimeEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(LocalDateTimeUtil.of(Long.parseLong(text)));
    }
}
