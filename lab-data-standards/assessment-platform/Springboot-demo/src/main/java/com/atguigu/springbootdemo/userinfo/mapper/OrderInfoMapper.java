package com.atguigu.springbootdemo.userinfo.mapper;

import com.atguigu.springbootdemo.userinfo.bean.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-27
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

}
