package com.kuzetech.bigdata.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CollectorController {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @GetMapping(path = "/send/{what}")
    public void sendFoo(@PathVariable String what) {
        Map<String, String> m = new HashMap<>();
        m.put("content", what);
        this.template.send("topic1", m);
    }

}
