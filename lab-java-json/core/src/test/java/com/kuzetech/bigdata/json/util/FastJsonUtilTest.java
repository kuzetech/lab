package com.kuzetech.bigdata.json.util;

import com.kuzetech.bigdata.json.domain.Account;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class FastJsonUtilTest {

    @Test
    void mapToObject() {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("username", "test");
        fieldMap.put("password", "123");
        Account account = (Account) FastJsonUtil.mapToObject(fieldMap, Account.class);
        System.out.println(account);
    }
}