package com.kuzetech.bigdata.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kuzetech.bigdata.json.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BaseTest {

    @Test
    void ObjectToString() {
        Account account = new Account();
        account.setUsername("demo");
        account.setPassword("demo");
        String userJson = JSON.toJSONString(account);
        log.info(userJson);
    }

    @Test
    void StringToObject() {
        String jsonStr = "{'password':'123456','username':'demo'}";
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Object noExistObject = jsonObject.get("empty");
        assertNull(noExistObject);
        String password = jsonObject.getString("password");
        System.out.println(password);
    }

    @Test
    void StringToList() {
        String jsonStr = "[{'password':'123123','username':'zhangsan'},{'password':'321321','username':'lisi'}]";
        List<Account> accounts = JSON.parseArray(jsonStr, Account.class);
        System.out.println("json字符串转List<Object>对象:"+accounts.toString());
    }
}