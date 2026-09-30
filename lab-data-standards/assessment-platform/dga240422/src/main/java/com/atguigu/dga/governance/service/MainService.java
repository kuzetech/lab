package com.atguigu.dga.governance.service;

/**
 * @author WEIYUNHUI
 * @date 2024/9/4 10:19
 */
public interface MainService {

    void startGovernanceAssess( String schemaName , String assessDate );

    void startGovernanceAssess( String assessDate );
    void startGovernanceAssess( );
}
