package com.atguigu.springbootdemo.customer.mapper;

import com.atguigu.springbootdemo.customer.bean.Customer;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/8/27 9:07
 *
 * 持久层组件
 *
 * @Mapper： 将类标识为持久层组件。
 *           Mybatis在运行时， 会通过动态代理的方式为我们生成代理实现类。 并将代理实现类的对象也管理到容器中
 *
 * Sql中的参数替代符:
 *
 *  #{}: 相对智能， 能提取参数中的属性 ，  会识别参数的类型会自动补全单引 , 本身字符串值中的特殊符号会被替换、转义
 *
 *  ${}: 参数原值（一般程序中动态组合sql） , 需要注意sql注入问题
 *
 */
@Mapper
@DS("test")
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select(" ${sql} ")
    List<Customer> selectCustomers( String sql ) ;

    //@Select("select id , username, age from t_customer where username like  #{username} and age = #{age} ")
    //List<Customer> selectCustomers(String username, Integer age) ;

    @Delete(" delete from customer where id = #{id}")
    //@DS("gmall")
    void deleteCustomer(Long id );

    @Update(" update customer set username = #{username} , age = #{age} where id = #{id} ")
    void updateCustomer(Customer customer);

    @Insert(" insert into customer (id, username, age) values (#{c.id} , #{c.username} , #{c.age})")
    void insertCustomer( @Param("c") Customer customer) ;

    @Select(" select id , username , age from customer where id = #{id} ")
    Customer getCustomer(Long id );
}

/*
class CustomerMapperImpl implements CustomerMapper{
    @Override
    public Customer getCustomer(Long id) {
        // 从数据库查询数据
        // JDBC
    }
}
 */