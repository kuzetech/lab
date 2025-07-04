package com.kuzetech.bigdata.json.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonObjectContent {
    private String username;
    private String password;
    private JSONObject data;
}
