package com.kuzetech.bigdata.study.restaurant.common.order;

import com.kuzetech.bigdata.study.restaurant.common.OperationResult;
import lombok.Data;

@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
