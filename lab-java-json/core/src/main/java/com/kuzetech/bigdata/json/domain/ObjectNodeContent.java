package com.kuzetech.bigdata.json.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectNodeContent {
    private String username;
    private String password;
    private ObjectNode data;
}
