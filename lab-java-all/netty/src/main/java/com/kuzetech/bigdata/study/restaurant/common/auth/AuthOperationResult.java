package com.kuzetech.bigdata.study.restaurant.common.auth;

import com.kuzetech.bigdata.study.restaurant.common.OperationResult;
import lombok.Data;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
