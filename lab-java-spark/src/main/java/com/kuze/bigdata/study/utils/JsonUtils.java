package com.kuze.bigdata.study.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

public class JsonUtils {
    public static boolean isJSONString(String content) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        if (!content.startsWith("{") || !content.endsWith("}")) {
            return false;
        }
        try {
            JSONObject.parse(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
