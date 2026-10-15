package com.kuzetech.bigdata.governance.assessor.security;

import com.kuzetech.bigdata.constant.Constant;
import com.kuzetech.bigdata.governance.assessor.Assessor;
import com.kuzetech.bigdata.governance.bean.AssessParam;
import com.kuzetech.bigdata.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author kuze
 * @date 2024/8/31 11:20
 */
@Component("TABLE_SECURITY_LEVEL")
public class TableSecurityLevelAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> TableSecurityLevelAssessor ");
        //判断是否设置过安全级别
        String securityLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getSecurityLevel();
        if(securityLevel == null || Constant.SECURITY_LEVEL_UNSET.equals(securityLevel)){
            //未设定安全级别
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("未设定安全级别");
        }
    }
}
