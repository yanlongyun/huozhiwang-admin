/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.huiyan.huozhiwang.service.impl;

import com.huiyan.huozhiwang.common.Constants;
import com.huiyan.huozhiwang.common.ServiceResultEnum;
import com.huiyan.huozhiwang.controller.vo.UserVO;
import com.huiyan.huozhiwang.dao.MallUserMapper;
import com.huiyan.huozhiwang.entity.MallUser;
import com.huiyan.huozhiwang.service.UserService;
import com.huiyan.huozhiwang.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            /*锁定标识字段，如果为0则未锁定，1锁定*/
            if (user.getLockedFlag() == 1) {
                /*锁定的禁止登陆*/
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示，所以只显示一部分
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            /*把user中得值复制到vo中*/
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(user, userVO);
            //设置购物车中的数量，存入用户信息。
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, userVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public UserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        UserVO userTemp = (UserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = mallUserMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            userFromDB.setNickName(MallUtils.cleanString(mallUser.getNickName()));
            userFromDB.setAddress(MallUtils.cleanString(mallUser.getAddress()));
            userFromDB.setIntroduceSign(MallUtils.cleanString(mallUser.getIntroduceSign()));
            if (mallUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                UserVO userVO = new UserVO();
                userFromDB = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(userFromDB, userVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, userVO);
                return userVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
