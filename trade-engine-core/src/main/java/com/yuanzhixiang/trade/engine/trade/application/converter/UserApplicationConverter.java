package com.yuanzhixiang.trade.engine.trade.application.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.mapstruct.Constants;
import com.yuanzhixiang.trade.engine.trade.web.model.request.UserGetPageRequest;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:34
 */
@Mapper(componentModel = Constants.CREATE_MODEL)
public interface UserApplicationConverter {

    @Mapping(source = "userType", target = "type")
    UserParam userGetPageRequestToParam(UserGetPageRequest userGetPageRequest);

}
