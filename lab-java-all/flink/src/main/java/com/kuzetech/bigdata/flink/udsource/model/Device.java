package com.kuzetech.bigdata.flink.udsource.model;

import com.kuzetech.bigdata.flink.utils.FakeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Device implements Serializable {
    private String id;
    private String model;

    public static Device generateDevice() {
        Device device = new Device();
        device.setId(UUID.randomUUID().toString());
        device.setModel(FakeUtil.generateDeviceModel());
        return device;
    }
}
