package com.yuanzhixiang.trade.engine.v1.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.v1.model.bo.HoldPositionBO;
import com.yuanzhixiang.trade.engine.v1.model.po.HoldPositionPO;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;

import cn.hutool.core.collection.CollectionUtil;
import com.yuanzhixiang.trade.engine.v1.converter.HoldPositionConverter;
import com.yuanzhixiang.trade.engine.v1.dao.HoldPositionMapper;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:46:23
 */
@Service
public class HoldPositionService {

    @Autowired
    private HoldPositionMapper holdPositionMapper;

    @Autowired
    private HoldPositionConverter holdPositionConverter;

    public List<HoldPositionBO> selectByUserId(Long userId) {
        LambdaQueryWrapper<HoldPositionPO> holdPositionWrapper = new LambdaQueryWrapper<>();
        holdPositionWrapper.eq(HoldPositionPO::getUserId, userId);
        List<HoldPositionPO> holdPositionPOList =
            holdPositionMapper.selectList(holdPositionWrapper);
        List<HoldPositionBO> holdPositionBOList = holdPositionConverter.poToBo(holdPositionPOList);

        if (CollectionUtil.isEmpty(holdPositionBOList)) {
            return Collections.emptyList();
        } else {
            holdPositionBOList = holdPositionBOList.stream()
                .filter(holdPositionBO ->
                    holdPositionBO.getPositionAmount() + holdPositionBO.getFrozenSellAmount() != 0)
                .collect(Collectors.toList());
        }

        return holdPositionBOList;
    }

    /**
     * 查询持仓，在事务中带行锁
     *
     * @param userId 用户 id
     * @param symbol 标的编码
     * @return 用户持仓
     */
    public HoldPositionPO selectHoldPosition(Long userId, String symbol) {
        LambdaQueryWrapper<HoldPositionPO> holdPositionWrapper = new LambdaQueryWrapper<>();
        holdPositionWrapper.eq(HoldPositionPO::getUserId, userId);
        holdPositionWrapper.eq(HoldPositionPO::getSymbol, symbol);
        return holdPositionMapper.selectOne(holdPositionWrapper);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void initUserHoldPosition(HoldPositionVO holdPositionVO) {

        HoldPositionBO holdPositionBO = holdPositionConverter.voToBo(holdPositionVO);

        HoldPositionPO holdPositionPO = holdPositionConverter.boToPo(holdPositionBO);

        HoldPositionPO oldHoldPosition =
            selectHoldPosition(holdPositionPO.getUserId(), holdPositionPO.getSymbol());

        // 初始化强行覆盖，不考虑并发问题
        if (oldHoldPosition != null) {
            // 之前有持仓直接覆盖
            holdPositionPO.setId(oldHoldPosition.getId());
            holdPositionMapper.updateById(holdPositionPO);
        } else {
            holdPositionMapper.insert(holdPositionPO);
        }
    }
}
