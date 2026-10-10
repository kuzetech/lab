package com.atguigu.springbootdemo.customer.service.impl;

import com.atguigu.springbootdemo.customer.bean.Customer;
import com.atguigu.springbootdemo.customer.mapper.CustomerMapper;
import com.atguigu.springbootdemo.customer.service.CustomerService;
import com.atguigu.springbootdemo.util.SqlUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/8/26 15:31
 *
 * 业务层的组件
 *
 * @Service: 将当前类标识为业务层的组件， 在spring容器中创建当前类的对象并管理 ,
 *           当前组件的对象在容器中有一个默认的名字:  类名首字母小写 CustomerServiceImpl => customerServiceImpl
 *           也可以具体指定名字: @Service("csi")
 */
@Service("csi")
@DS("test")
public class CustomerServiceImpl extends ServiceImpl< CustomerMapper , Customer> implements CustomerService {

    @Autowired
    CustomerMapper customerMapper ;

    /**
     * 动态条件 、 sql注入
     */
    @Override
    public List<Customer> getCustomers(String username, Integer age) {

        //根据条件情况，动态拼接sql
        //select id , username, age from t_customer where username like  #{username} and age = #{age}
        /*StringBuilder sqlBuilder = new StringBuilder("select id , username, age from t_customer where 1=1  ") ;
        if( username != null && !username.trim().isEmpty()){
            sqlBuilder.append(" and username like '%"+ SqlUtil.filterUnsafeSql(username) +"%' ");
        }

        if( age!= null ){
            sqlBuilder.append(" and age = " + age ) ;
        }

        sqlBuilder.append(" and username !='zhangsan' ") ;

        List<Customer> customers = customerMapper.selectCustomers(sqlBuilder.toString());*/

        //MP提供的用于条件判断的Wrapper对象
        /*
        // list() = select id , username, age from t_customer
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        if( username != null && !username.trim().isEmpty()){
           queryWrapper.like("username"  ,  SqlUtil.filterUnsafeSql(username) ) ;
        }

        if( age!= null ){
            queryWrapper.eq("age" , age ) ;
        }
        List<Customer> customers = this.list(
                queryWrapper
        );
         */

        List<Customer> customers = this.list(
                new QueryWrapper<Customer>()
                        .like(username != null && !username.trim().isEmpty(), "username", SqlUtil.filterUnsafeSql(username))
                        .eq(age != null, "age", age)
        );

        return customers;
    }

    @Override
    public void deleteCustomer(Long id) {
        // 业务处理的代码....

        // 调用数据访问层 读写 数据库表中的数据
        //customerMapper.deleteCustomer(id);

        //调用MP提供的方法
        customerMapper.deleteById(id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        // 业务处理的代码....

        // 调用数据访问层 读写 数据库表中的数据
        //customerMapper.updateCustomer(customer);

        //调用MP提供的方法
        customerMapper.updateById(customer) ;
    }

    @Override
    public void saveCustomer(Customer customer) {
        // 业务处理的代码....

        // 调用数据访问层 读写 数据库表中的数据
        //customerMapper.insertCustomer(customer);

        //调用MP提供的方法
        customerMapper.insert(customer) ;
    }

    @Override
    public Customer getCustomer(Long id) {
        // 业务处理的代码....

        // 调用数据访问层 读写 数据库表中的数据
        // 模拟从数据库中读取到的数据
        //Customer customer = new Customer(id, "zhangxiaosan", 15);

        //从数据库表中查询数据
        //Customer customer = customerMapper.getCustomer(id);

        //调用MP提供的方法
        Customer customer = customerMapper.selectById(id);

        return customer;
    }
}
