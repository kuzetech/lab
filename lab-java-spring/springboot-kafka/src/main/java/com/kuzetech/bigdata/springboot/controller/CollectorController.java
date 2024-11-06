package com.kuzetech.bigdata.springboot.controller;

import com.kuzetech.bigdata.springboot.bean.KafkaInfo;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class CollectorController {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @GetMapping(path = "/send/{what}")
    public ResponseEntity<KafkaInfo> sendFoo(@PathVariable String what) throws ExecutionException, InterruptedException {
        Map<String, String> m = new HashMap<>();
        m.put("content", what);
        CompletableFuture<SendResult<Object, Object>> future = this.template.send("topic1", m);
        SendResult<Object, Object> result = future.get();
        RecordMetadata recordMetadata = result.getRecordMetadata();
        return ResponseEntity.ok(new KafkaInfo(recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
    }

}
