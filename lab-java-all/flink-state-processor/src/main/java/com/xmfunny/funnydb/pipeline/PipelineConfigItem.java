package com.xmfunny.funnydb.pipeline;

import com.xmfunny.funnydb.metadata.Processors;
import lombok.Data;

import java.util.Map;

@Data
public class PipelineConfigItem {
    private Map<String, Processors> typeMap;
}
