package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;

/**
 * 反序列化时将时间戳反序列化为 LocalDate
 *
 * @author ZhiXiang Yuan
 * @date 2020/09/24 00:19
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        long timestamp = p.getValueAsLong();
        if (timestamp > 0) {
            return LocalDateTimeUtil.of(timestamp).toLocalDate();
        } else {
            return null;
        }
    }
}
