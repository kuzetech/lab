package com.kuzetech.bigdata.flink.fake;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class FakeUserTest {

    @Test
    public void generateUser() {
        FakeUser fakeUser = FakeUser.generateUser();
        log.info(fakeUser.toString());
    }
}