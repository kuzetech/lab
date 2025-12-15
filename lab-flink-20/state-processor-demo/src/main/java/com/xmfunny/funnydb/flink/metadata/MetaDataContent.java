package com.xmfunny.funnydb.flink.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataContent implements Serializable {
    private Map<String, PipelineConfigItem> appMap;
}
