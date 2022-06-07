package com.yuanzhixiang.trade.engine.trade.web.converter;

/**
 * @author ZhiXiang Yuan
 * @date 2020/09/21 20:15
 */
public interface WebConverter<VO, BO, DO> extends VoBoConverter<VO, BO>, BoPoConverter<BO, DO> {


}

