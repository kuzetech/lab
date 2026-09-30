package com.atguigu.springbootdemo.userinfo.service.impl;

import com.atguigu.springbootdemo.userinfo.bean.OrderInfo;
import com.atguigu.springbootdemo.userinfo.mapper.OrderInfoMapper;
import com.atguigu.springbootdemo.userinfo.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

}
