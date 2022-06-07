package com.yuanzhixiang.trade.engine.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.yuanzhixiang.trade.engine.trade.web.model.Response;
import com.yuanzhixiang.trade.engine.v1.converter.HoldPositionConverter;
import com.yuanzhixiang.trade.engine.v1.model.bo.HoldPositionBO;
import com.yuanzhixiang.trade.engine.v1.model.vo.HoldPositionVO;
import com.yuanzhixiang.trade.engine.v1.service.HoldPositionService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhixiang.yuan
 * @since 2021/02/16 11:43:59
 */
@Slf4j
public class HoldPositionController {

    @Autowired
    private HoldPositionService holdPositionService;

    @Autowired
    private HoldPositionConverter holdPositionConverter;

    /**
     * 查询用户当前持仓
     *
     * @param userId 用户 id
     * @return 当前持仓
     */
    @GetMapping
    public Response<List<HoldPositionVO>> selectByUserId(@RequestParam("userId") Long userId) {
        List<HoldPositionBO> resultBOList = holdPositionService.selectByUserId(userId);

        return Response.success(holdPositionConverter.boToVo(resultBOList));
    }

    /**
     * 初始化用户持仓
     *
     * @param holdPositionVO 持仓信息
     */
    @PostMapping
    public Response<String> initUserHoldPosition(@RequestBody HoldPositionVO holdPositionVO) {
        holdPositionService.initUserHoldPosition(holdPositionVO);
        return Response.success("处理成功");
    }
}
