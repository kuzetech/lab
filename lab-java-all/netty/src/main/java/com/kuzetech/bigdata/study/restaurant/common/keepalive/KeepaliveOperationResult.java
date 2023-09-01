package com.kuzetech.bigdata.study.restaurant.common.keepalive;

import com.kuzetech.bigdata.study.restaurant.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
