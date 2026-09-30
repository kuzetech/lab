package com.atguigu.springbootdemo.userinfo.mapper;

import com.atguigu.springbootdemo.userinfo.bean.UserInfo;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@Mapper
@DS("gmall")
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
