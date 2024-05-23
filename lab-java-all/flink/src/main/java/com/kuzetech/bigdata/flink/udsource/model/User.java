package com.kuzetech.bigdata.flink.udsource.model;

import com.kuzetech.bigdata.flink.utils.FakeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private String id;
    private String name;
    private String country;

    public static User generateUser() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName(FakeUtil.generateUserName());
        user.setCountry(FakeUtil.generateCountry());
        return user;
    }
}
