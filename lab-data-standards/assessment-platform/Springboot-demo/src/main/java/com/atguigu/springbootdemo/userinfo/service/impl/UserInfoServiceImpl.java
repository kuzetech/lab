package com.atguigu.springbootdemo.userinfo.service.impl;

import com.atguigu.springbootdemo.userinfo.bean.UserInfo;
import com.atguigu.springbootdemo.userinfo.mapper.UserInfoMapper;
import com.atguigu.springbootdemo.userinfo.service.UserInfoService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@Service
@DS("gmall")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
