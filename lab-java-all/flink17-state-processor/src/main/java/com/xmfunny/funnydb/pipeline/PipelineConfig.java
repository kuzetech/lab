package com.xmfunny.funnydb.pipeline;

import lombok.Data;

import java.util.Map;

@Data
public class PipelineConfig {
    private Map<String, PipelineConfigItem> appMap;
}
