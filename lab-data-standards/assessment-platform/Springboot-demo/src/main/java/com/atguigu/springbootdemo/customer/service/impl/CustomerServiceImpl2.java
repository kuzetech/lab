package com.atguigu.springbootdemo.customer.service.impl;

import com.atguigu.springbootdemo.customer.bean.Customer;
import com.atguigu.springbootdemo.customer.service.CustomerService;
import org.springframework.stereotype.Service;

/**
 * @author WEIYUNHUI
 * @date 2024/8/26 15:45
 *
 *
 */
//@Service("csi2")
public class CustomerServiceImpl2  /*implements CustomerService*/ {
    //@Override
    public Customer getCustomer(Long id) {
        // 业务处理的代码....

        // 调用数据访问层 读写 数据库表中的数据
        // 模拟从数据库中读取到的数据
        Customer customer = new Customer(id, "lixiaosi", 10);

        return customer;
    }
}
