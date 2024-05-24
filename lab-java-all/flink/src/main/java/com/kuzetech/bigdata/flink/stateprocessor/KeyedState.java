package com.kuzetech.bigdata.flink.stateprocessor;

import com.kuzetech.bigdata.flink.stateprocessor.model.DeviceModelStatistics;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeyedState {
    private DeviceModelStatistics statistics;
}
