package com.atguigu.dga.governance.service.impl;

import com.atguigu.dga.governance.service.*;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author WEIYUNHUI
 * @date 2024/9/4 10:21
 */
@Service
public class MainServiceImpl implements MainService {

    @Autowired
    TableMetaInfoService tableMetaInfoService ;

    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService;

    @Autowired
    GovernanceAssessTableService governanceAssessTableService ;

    @Autowired
    GovernanceAssessTecOwnerService governanceAssessTecOwnerService ;

    @Autowired
    GovernanceAssessGlobalService governanceAssessGlobalService ;

    @Value("${default.dw.name}")
    private String defaultDwName ;

    @Override
    public void startGovernanceAssess(String schemaName, String assessDate) {
        //1.提取元数据
        tableMetaInfoService.initTableMetaInfo(schemaName , assessDate);

        //2.考评
        governanceAssessDetailService.mainAssess( assessDate );

        //3.核算分数
        governanceAssessTableService.calcTableSorce( assessDate );
        governanceAssessTecOwnerService.calcTecOwnerScore( assessDate );
        governanceAssessGlobalService.calcGlobalScore( assessDate );
    }

    @Override
    public void startGovernanceAssess(String assessDate) {
        startGovernanceAssess( defaultDwName , assessDate  );
    }

    @Override
    @Scheduled(cron = "0 40 10 * * *")
    public void startGovernanceAssess() {
        String schemaName = defaultDwName ;
        //定时调度一定是当天，直接获取当天的时间即可
        String assessDate = "2023-05-02" ;
        startGovernanceAssess(schemaName ,assessDate );
    }
}
