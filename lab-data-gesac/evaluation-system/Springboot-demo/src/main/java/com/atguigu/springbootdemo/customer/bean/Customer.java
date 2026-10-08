package com.atguigu.springbootdemo.customer.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author WEIYUNHUI
 * @date 2024/8/26 14:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value="t_customer")  // 告诉MP数据中表的名字
public class Customer {

    @TableId(value = "id" , type = IdType.AUTO) // 告诉MP该字段为数据库表的自增主键，
    private Long id ;

    private String username;

    private Integer age ;

}
