package com.kuzetech.bigdata.flink.fake;

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
public class FakeUser implements Serializable {
    private String id;
    private String name;
    private String country;

    public static FakeUser generateUser() {
        FakeUser user = new FakeUser();
        user.setId(UUID.randomUUID().toString());
        user.setName(FakeUtil.generateUserName());
        user.setCountry(FakeUtil.generateCountry());
        return user;
    }
}
