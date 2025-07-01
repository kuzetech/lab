package com.kuzetech.bigdata.json.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountGroup {
    private String name;
    private List<Account> users = new ArrayList<>();
}
