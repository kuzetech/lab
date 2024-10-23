package com.xmfunny.funnydb.flink.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmfunny.funnydb.pipeline.PipelineConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataContent {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Map<String, PipelineConfigItem> appMap;

    public static MetaDataContent generateFromPipelineConfig(PipelineConfig pipelineConfig) throws IOException {
        byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(pipelineConfig);
        return OBJECT_MAPPER.readValue(bytes, MetaDataContent.class);
    }
}
