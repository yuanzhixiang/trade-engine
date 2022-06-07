package com.yuanzhixiang.trade.engine.v1.converter;

import com.yuanzhixiang.trade.engine.trade.web.converter.WebConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.WalletBO;
import com.yuanzhixiang.trade.engine.v1.model.po.WalletPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.WalletVO;
import org.mapstruct.Mapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 17:37:31
 */
@Mapper(componentModel = "spring")
public interface WalletWebConverter extends WebConverter<WalletVO, WalletBO, WalletPO> {
}
