package com.atguigu.springbootdemo.userinfo.controller;

import com.atguigu.springbootdemo.userinfo.bean.UserInfo;
import com.atguigu.springbootdemo.userinfo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@RestController
@RequestMapping("/userinfo")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 查询
     *
     * 客户端的请求: http://localhost:8080/userinfo/get/1
     *
     * @param id
     * @return
     */

    @GetMapping("/get/{id}")
    public UserInfo getUserInfo( @PathVariable("id") Long id ){
        UserInfo userInfo = userInfoService.getById(id);

        return userInfo ;
    }

}
