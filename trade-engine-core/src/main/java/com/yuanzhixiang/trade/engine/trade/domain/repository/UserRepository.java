package com.yuanzhixiang.trade.engine.trade.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.user.UserValObj;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.param.UserRobotParam;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialUserPO;
import com.yuanzhixiang.trade.engine.trade.domain.repository.po.FinancialUserRobotPO;

import cn.hutool.core.util.StrUtil;
import com.yuanzhixiang.trade.engine.trade.domain.model.valobj.PageResult;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialUserMapper;
import com.yuanzhixiang.trade.engine.trade.domain.repository.dao.mapper.FinancialUserRobotMapper;
import com.yuanzhixiang.trade.engine.trade.utils.PageUtil;

/**
 * @author ZhiXiang Yuan
 * @date 2021/06/12 14:14
 */
@Repository
public class UserRepository {

    @Autowired
    private FinancialUserMapper financialUserMapper;

    @Autowired
    private FinancialUserRobotMapper financialUserRobotMapper;

    /**
     * 创建用户
     *
     * @param userValObj 用户值对象
     * @return true 创建成功 / false 创建失败
     */
    public Long postUser(UserValObj userValObj) {
        FinancialUserPO financialUserPO = new FinancialUserPO();
        financialUserPO.setType(userValObj.getType());
        financialUserPO.setName(userValObj.getName());
        financialUserMapper.insert(financialUserPO);
        return financialUserPO.getId();
    }

    /**
     * 创建机器人用户和标的的变细
     *
     * @param userId   用户 id
     * @param symbolId 标的 id
     */
    public void postUserRobot(Long userId, Long symbolId) {
        FinancialUserRobotPO financialUserRobotPO = new FinancialUserRobotPO();
        financialUserRobotPO.setUserId(userId);
        financialUserRobotPO.setSymbolId(symbolId);
        financialUserRobotMapper.insert(financialUserRobotPO);
    }

    /**
     * 按条件查询用户
     *
     * @param userParam 查询用户的参数
     * @return 查询出的用户
     */
    public Optional<FinancialUserPO> getUser(UserParam userParam) {
        LambdaQueryWrapper<FinancialUserPO> condition = paramToWrapper(userParam);
        return Optional.ofNullable(financialUserMapper.selectOne(condition));
    }

    /**
     * 分页查询用户
     *
     * @param userParam 查询用户的参数
     * @return 分页查询结果
     */
    public PageResult<FinancialUserPO> getUserPage(UserParam userParam) {
        LambdaQueryWrapper<FinancialUserPO> condition = paramToWrapper(userParam);
        return PageUtil.pageToPageResult(financialUserMapper
            .selectPage(PageUtil.pageParamToPage(userParam), condition));
    }

    private LambdaQueryWrapper<FinancialUserPO> paramToWrapper(UserParam userParam) {
        LambdaQueryWrapper<FinancialUserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userParam.getId() != null, FinancialUserPO::getId, userParam.getId());
        wrapper.eq(userParam.getType() != null, FinancialUserPO::getType, userParam.getType());
        wrapper.like(StrUtil.isNotBlank(userParam.getName()), FinancialUserPO::getName, userParam.getName());
        return wrapper;
    }

    /**
     * 查询机器人列表
     *
     * @param userRobotParam 查询机器人参数
     * @return 机器人列表
     */
    public List<FinancialUserRobotPO> getUserRobotList(UserRobotParam userRobotParam) {
        LambdaQueryWrapper<FinancialUserRobotPO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialUserRobotPO::getSymbolId, userRobotParam.getSymbolId());
        return financialUserRobotMapper.selectList(condition);
    }

    /**
     * 删除机器人用户
     *
     * @param userId 用户 id
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteUserRobot(Long userId) {
        LambdaQueryWrapper<FinancialUserRobotPO> condition = new LambdaQueryWrapper<>();
        condition.eq(FinancialUserRobotPO::getUserId, userId);
        return financialUserRobotMapper.delete(condition) == 1;
    }

    /**
     * 删除用户
     *
     * @param id 主键
     * @return true 删除成功 / false 删除失败
     */
    public boolean deleteUser(Long id) {
        return financialUserMapper.deleteById(id) == 1;
    }
}
