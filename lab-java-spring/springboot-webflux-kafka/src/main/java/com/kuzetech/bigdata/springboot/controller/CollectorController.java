package com.kuzetech.bigdata.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
public class CollectorController {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @GetMapping(path = "/send/{content}")
    public Mono<String> sendToKafka(@PathVariable String content) {
        Map<String, String> m = new HashMap<>();
        m.put("content", content);
        CompletableFuture<SendResult<Object, Object>> future = this.template.send("topic1", m);
        return Mono.fromFuture(future).map(new Function<SendResult<Object, Object>, String>() {
            @Override
            public String apply(SendResult<Object, Object> r) {
                return r.getRecordMetadata().toString();
            }
        });
    }

}
