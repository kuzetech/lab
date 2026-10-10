package com.atguigu.dga;

import com.atguigu.dga.governance.bean.GovernanceAssessGlobal;
import com.atguigu.dga.governance.service.*;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.atguigu.dga.meta.service.impl.TableMetaInfoServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Dga240422ApplicationTests {

    @Autowired
    TableMetaInfoService tableMetaInfoService ;

    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService ;

    @Autowired
    GovernanceAssessTableService governanceAssessTableService ;

    @Autowired
    GovernanceAssessTecOwnerService governanceAssessTecOwnerService ;

    @Autowired
    GovernanceAssessGlobalService governanceAssessGlobalService;

    @Autowired
    MainService mainService;

    @Test
    public void testStartGovernanceAssess(){
        mainService.startGovernanceAssess("gmall" , "2023-05-02");
    }

    @Test
    public void testGobalScore(){
        governanceAssessGlobalService.calcGlobalScore("2023-05-02");
    }

    @Test
    public void testTecOwnerScore(){
        governanceAssessTecOwnerService.calcTecOwnerScore("2023-05-02");
    }

    @Test
    public void testTableScore(){
        governanceAssessTableService.calcTableSorce( "2023-05-02");
    }

    /**
     * 测试主考评
     */
    @Test
    public void testMainAssess(){
        governanceAssessDetailService.mainAssess("2023-05-02");
    }

    /**
     * 测试提取元数据
     */
    @Test
    public void testInitTableMetaInfo(){
        tableMetaInfoService.initTableMetaInfo("gmall" , "2023-05-02");
    }


    /**
     * 测试Hive元数据客户端的获取
     */

    @Test
    public void testHiveClient(){
        //tableMetaInfoService.createHiveMetaStoreClient();
    }

}
