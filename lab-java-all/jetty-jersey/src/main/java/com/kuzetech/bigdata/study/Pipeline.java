package com.kuzetech.bigdata.study;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class Pipeline implements Serializable {

    // 这里需要使用 org.hibernate.validator.constraints.NotBlank 才能起作用
    // 如果使用 javax.validation.constraints.NotBlank 没有效果
    @NotBlank(message = "kafkaServers 字段不能为空")
    private String kafkaServers;
    @NotNull(message = "sparkAllowExceptionInsert 字段不能为空")
    private Boolean sparkAllowExceptionInsert;

    public String getKafkaServers() {
        return kafkaServers;
    }

    public void setKafkaServers(String kafkaServers) {
        this.kafkaServers = kafkaServers;
    }

    public Boolean getSparkAllowExceptionInsert() {
        return sparkAllowExceptionInsert;
    }

    public void setSparkAllowExceptionInsert(Boolean sparkAllowExceptionInsert) {
        this.sparkAllowExceptionInsert = sparkAllowExceptionInsert;
    }
}
