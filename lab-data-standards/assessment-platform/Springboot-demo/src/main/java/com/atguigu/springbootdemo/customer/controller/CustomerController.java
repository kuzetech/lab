package com.atguigu.springbootdemo.customer.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.springbootdemo.customer.bean.Customer;
import com.atguigu.springbootdemo.customer.service.CustomerService;
import com.atguigu.springbootdemo.customer.service.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/8/26 11:43
 *
 * 控制层组件
 *
 * @RestController： 将当前类标记为控制层组件 ， 将当前类的对象管理到SpringIOC容器中
 *
 */
@RestController
public class CustomerController {

    //创建业务层对象
    //CustomerServiceImpl customerService = new CustomerServiceImpl();

    //使用了spring框架后，只需要声明想要的对象即可。

    //自动装配
    @Autowired
    //@Qualifier("csi")
    CustomerService customerService ;


    /**
     * 演示动态参数 、 sql注入问题
     *
     * 客户端的请求:  http://localhost:8080/getcustomers?username=zhang&age=20
     *
     * username和age两个参数可有可无， 且username采用模糊匹配
     */

    @GetMapping("/getcustomers")
    public String getCustomers( @RequestParam(value ="username" , required = false ) String username ,
                                @RequestParam(value ="age" , required = false) Integer age ){
        List<Customer> customers = customerService.getCustomers(username, age);

        return JSON.toJSONString( customers ) ;
    }

    /**
     * 删除customer
     *
     * 客户端请求: http://localhost:8080/deletecustomer/1003
     */
    @PostMapping("/deletecustomer/{id}")
    public String deleteCustomer( @PathVariable("id") Long id ){
        //customerService.deleteCustomer(id);

        //调用MP提供的方法
        customerService.removeById(id);

        return "success";
    }

    /**
     * 修改customer
     *
     * 客户端的请求: http://localhost:8080/updatecustomer
     *
     * 请求体参数: {"id":1003, "username":"wangxiaowu" , "age":13}
     */

    @PostMapping("/updatecustomer")
    public String updatecustomer( @RequestBody  Customer customer){
        //调用service
        //customerService.updateCustomer(customer);

        //调用MP提供的方法
        customerService.updateById(customer) ;

        return "success" ;
    }

    /**
     * 新增 customer
     *
     * 客户端的请求: http://localhost:8080/savecustomer
     *
     * 请求体参数: {"id":1003, "username":"wangwu" , "age":30}
     */

    @PostMapping("/savecustomer")
    public String savecustomer( @RequestBody  Customer customer){
        //调用service
        //customerService.saveCustomer(customer);

        //调用MP提供的方法
        customerService.save(customer) ;

        return "success" ;
    }


    /**
     * 查询 customer
     *
     * 客户端的请求: http://localhost:8080/getcustomer/1001
     */
    @GetMapping("/getcustomer/{id}")
    public Customer getcustomer(@PathVariable(value = "id") Long id ){
        //调用服务层(业务层)

        //创建业务层对象
        //CustomerServiceImpl customerService = new CustomerServiceImpl();
        //Customer customer = customerService.getCustomer(id);

        //调用MP提供的方法
        Customer customer = customerService.getById(id);

        return customer ; // 框架会采用jackson工具将对象转换成json格式的字符串返回
    }



    /**
     * 常见的状态码
     *
     *   200 : 请求成功
     *   400 : 请求参数错误
     *   404 : 请求资源不存在
     *   405 : 请求方式不正确
     *   500 : 服务器内部错误(到控制台查看异常)
     *
     * 客户端的请求: http://localhost:8080/code?username=zhangsan&age=22
     */

    @GetMapping("/code")
    public String code( @RequestParam(value= "username" , required = false , defaultValue = "wuming") String username,
                        @RequestParam(value="age" , required = false ,defaultValue = "18") Integer age ){
        System.out.println("username : " + username  + " , age : " + age   );
        if(1 == 1 ){
            throw new RuntimeException("抛异常了....") ;
        }
        return "success" ;
    }


    /**
     * 响应结果的格式
     *
     * 通常情况下， 都是以json格式的字符串的形式返回结果到客户端
     *
     *
     * 客户端的请求: http://localhost:8080/result
     */

    @GetMapping("/result")
    public String  result(){
        //标记类型，直接返回单词即可。
        //return "ok" ; //"error" , "success"

        //如果要返回从后台查询到的一些数据， 一般会将数据转换成json格式的字符串返回

        Customer customer = new Customer(1001L,"zhangsan" , 35);

        //借助于 fastjson 工具 手动转换
        return  JSON.toJSONString(customer);

        //框架进行转换 ， 框架默认使用jackson工具将java对象转换成json格式字符串返回
        //return customer ;
    }



    /**
     * 请求方式:
     *   GET:  一般用于读请求
     *
     *   POST: 一般用于写请求
     *
     *  客户端的请求:  http://localhost:8080/requestmethod
     */
    //@RequestMapping(value = "/requestmethod" , method = RequestMethod.GET)
    //@RequestMapping(value = "/requestmethod" , method = RequestMethod.POST)
    @GetMapping(value = "/requestmethod")
    //@PostMapping(value = "/requestmethod")
    public String requestmethod(){
        return "success" ;
    }



    /**
     * 请求参数的三种方式：
     *   1. 键值对参数
     *       https://search.jd.com/Search?
     *        keyword=手机
     *        &
     *        enc=utf-8
     *        &
     *        pvid=8783ddc6ee674306ac090d14f4b0e4e8
     *
     *      客户端的请求: http://localhost:8080/param1?username=zhangsan&age=20
     *
     *      @RequestParam: 将请求中的键值对参数 映射到 请求处理方法的形参上。
     *
     *   2. 拼接到请求路径中的参数
     *
     *        客户端的请求:  http://localhost:8080/param2/zhangsan/20
     *
     *        @PathVariable： 将请求路径中的参数 映射到 请求处理方法的形参上。
     *
     *   3. 请求体参数
     *
     *        客户端的请求: http://localhost:8080/param3
     *        请求体的参数: {"id":1001, "username":"zhaoliu","age":26}
     *
     *        @RequestBody: 将请求体中的参数映射到请求方法的形参对象的属性上。
     *                      要求: 请求参数名 与 对象的属性名一致。
     *
     */

    @RequestMapping(value = "/param3")
    public String param3( @RequestBody  Customer customer){
        System.out.println("customer: " + customer );

        return "success" ;
    }

    @RequestMapping(value = "/param2/{name}/{age}")
    public String param2( @PathVariable(value="name") String name , @PathVariable(value = "age") Integer age ){
        System.out.println("name : " + name   + " , age : " + age   );
        return "success" ;
    }

    @RequestMapping( "/param1")
    public String  param1(@RequestParam(value= "username") String username, @RequestParam(value="age") Integer age  ){
        System.out.println("username : " + username  + " , age : " + age   );
        return "success";
    }


    /**
     * 客户端请求: http://localhost:8080/hello
     *
     * 处理请求的方法
     */
    @RequestMapping(value="/hello")
    public String hello(){
        System.out.println("客户端有请求发送过来了.....");

        return "success";
    }


}
