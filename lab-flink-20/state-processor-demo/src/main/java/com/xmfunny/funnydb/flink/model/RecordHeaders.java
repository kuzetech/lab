package com.xmfunny.funnydb.flink.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.xmfunny.funnydb.flink.Constant.INGEST_RECORD_APP_KEY;
import static com.xmfunny.funnydb.flink.Constant.INGEST_RECORD_TYPE_KEY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordHeaders implements Serializable {
    @JsonProperty(INGEST_RECORD_APP_KEY)
    private String app;
    @JsonProperty(INGEST_RECORD_TYPE_KEY)
    private String type;
}
