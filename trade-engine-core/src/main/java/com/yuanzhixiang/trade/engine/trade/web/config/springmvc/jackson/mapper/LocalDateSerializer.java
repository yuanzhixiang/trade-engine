package com.yuanzhixiang.trade.engine.trade.web.config.springmvc.jackson.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

/**
 * 序列化时将 LocalDate 序列化为时间戳
 *
 * @author ZhiXiang Yuan
 * @date 2020/09/24 00:22
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        if (Objects.nonNull(value)) {
            gen.writeNumber(value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }
}
