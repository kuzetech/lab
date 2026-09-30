package com.atguigu.dga.governance.assessor.calc;

import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.governance.bean.TDsTaskInstance;
import com.atguigu.dga.governance.service.TDsTaskInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/9/2 15:18
 */
@Component("TABEL_FAIL")
public class TableFailAssessor extends Assessor {

    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService ;
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableNoAccessAssessor ");

        //排除ODS的表

        //查询当日失败的实例
        String name = assessParam.getTableMetaInfo().getSchemaName() + "." + assessParam.getTableMetaInfo().getTableName();

        List<TDsTaskInstance> failTDsTaskInstanceList
                    = tDsTaskInstanceService.getFailTDsTaskInstanceList( name , assessParam.getAssessDate());
        if(failTDsTaskInstanceList.size() > 0 ){
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("当日计算有报错");
        }
    }
}
