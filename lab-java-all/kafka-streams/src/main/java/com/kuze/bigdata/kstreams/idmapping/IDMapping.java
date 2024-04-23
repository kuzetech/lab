package com.kuze.bigdata.kstreams.idmapping;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class IDMapping {
    private String deviceId;
    private String idCard;
    private String phone;
}
