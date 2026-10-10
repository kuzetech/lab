package com.atguigu.springbootdemo.customer.service;

import com.atguigu.springbootdemo.customer.bean.Customer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/8/26 15:29
 *
 * 业务层的接口
 */
public interface CustomerService extends IService<Customer> {

    List<Customer> getCustomers(String username , Integer age );

    void deleteCustomer(Long id );

    void updateCustomer( Customer customer );

    void saveCustomer( Customer customer );

    Customer getCustomer(Long id );
}
