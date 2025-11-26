package com.kuzetech.bigdata.design.structure.proxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserController implements IUserController {

    @Override
    public String login(String user, String password) {
        log.info("user success login");
        return "success";
    }

}
