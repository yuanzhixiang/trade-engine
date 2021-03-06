package com.yuanzhixiang.trade.engine.trade.web;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuanzhixiang.trade.engine.trade.application.UserAggregateApplication;
import com.yuanzhixiang.trade.engine.trade.domain.model.aggregate.UserAggregate;
import com.yuanzhixiang.trade.engine.trade.domain.model.entity.account.AccountTradeEntity;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserType;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserValObj;
import com.yuanzhixiang.trade.engine.trade.exception.ExceptionHelper;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContext;
import com.yuanzhixiang.trade.engine.trade.web.model.common.BusinessContextUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.request.UserGetPageRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserRobotGroupPostRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.request.user.UserTraderPostRequest;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.AccountSymbolVO;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.AccountTradeVO;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.CanCancelOrderVO;
import com.yuanzhixiang.trade.engine.trade.web.model.vo.UserVO;

import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.web.model.Response;

import com.yuanzhixiang.trade.engine.trade.domain.model.factory.user.UserFactory;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.accounttrade.AccountTradeValObj;

import lombok.extern.slf4j.Slf4j;

/**
 * ????????????
 *
 * @author ZhiXiang Yuan
 * @date 2021/06/13 10:24
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserAggregateApplication userAggregateApplication;

    @Autowired
    private UserFactory userFactory;

    /**
     * ??????????????????????????????
     *
     * @param request ??????????????????
     * @return ??????????????????
     */
    @GetMapping("/getUserPage")
    public Response<PageResult<UserVO>> getUserPage(UserGetPageRequest request) {
        PageResult<UserAggregate> userPage = userAggregateApplication.getUserPage(request);
        return Response.success(PageResult.getPageResult(userPage, userAggregates ->
            userAggregates.stream().map(this::aggregateToVo).collect(Collectors.toList())));
    }

    /**
     * ????????????
     *
     * @param request ??????????????????
     */
    @PostMapping("/postUser")
    public Response<String> postUser(@Valid @RequestBody UserTraderPostRequest request) {

        // ??????????????????????????????????????????????????????
        if (UserType.TRADER.name().equals(request.getUserType())) {
            if (StrUtil.isBlank(request.getLoginAccount()) || StrUtil.isBlank(request.getPassword())) {
                ExceptionHelper.throwSystemException("??????????????????????????????");
            }
        }

        userFactory.postUser(request);
        return Response.success("????????????");
    }

    /**
     * ??????????????????
     *
     * @param request ????????????
     */
    @PostMapping("/postUserRobotGroup")
    public Response<String> postUserRobotGroup(@Valid @RequestBody UserRobotGroupPostRequest request) {
        userFactory.postUserRobotGroup(request);
        return Response.success("????????????");
    }


    /**
     * ????????????
     *
     * @param id ????????????
     */
    @DeleteMapping("/deleteUser")
    public Response<Void> deleteUser(@RequestParam("id") Long id) {
        UserAggregate user = userFactory.getUser(id);
        user.deleteUser();
        return Response.success();
    }

    /**
     * ??????????????? vo
     *
     * @param userAggregate ?????????
     * @return vo
     */
    private UserVO aggregateToVo(UserAggregate userAggregate) {
        UserVO userVO = new UserVO();

        UserValObj userValObj = userAggregate.getUserValObj();
        userVO.setId(userAggregate.getId());
        userVO.setName(userValObj.getName());
        userVO.setUserType(userValObj.getType());

        return userVO;
    }

    /**
     * ????????????????????????
     *
     * @return ??????????????????
     */
    @GetMapping("/getAccountTrade")
    public Response<AccountTradeVO> getAccountTrade() {
        BusinessContext businessContext = BusinessContextUtil.get();
        UserAggregate user = businessContext.getUser();
        AccountTradeEntity accountTradeEntity = user.getAccountTradeEntity();
        AccountTradeValObj accountTradeValObj = accountTradeEntity.getAccountTradeValObj();

        AccountTradeVO accountTradeVO = new AccountTradeVO();
        accountTradeVO.setBalance(accountTradeValObj.getBalance());
        accountTradeVO.setFrozenFund(accountTradeValObj.getFrozenFund());
        return Response.success(accountTradeVO);
    }

    /**
     * ????????????????????????
     *
     * @return ????????????
     */
    @GetMapping("/getAccountSymbolPage")
    public Response<PageResult<AccountSymbolVO>> getAccountSymbolPage() {
        BusinessContext businessContext = BusinessContextUtil.get();
        UserAggregate user = businessContext.getUser();
        return Response.success(userAggregateApplication.getAccountSymbolPage(user.getId()));
    }

    /**
     * ?????????????????????
     *
     * @return ???????????????
     */
    @GetMapping("/getCanCancelOrderList")
    public Response<List<CanCancelOrderVO>> getCanCancelOrderList() {
        BusinessContext businessContext = BusinessContextUtil.get();
        UserAggregate user = businessContext.getUser();
        List<CanCancelOrderVO> canCancelOrderList = userAggregateApplication.getCanCancelOrderList(user.getId());
        return Response.success(canCancelOrderList);
    }


}
