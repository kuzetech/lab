package com.kuzetech.bigdata.governance.assessor.spec;

import com.kuzetech.bigdata.constant.Constant;
import com.kuzetech.bigdata.governance.assessor.Assessor;
import com.kuzetech.bigdata.governance.bean.AssessParam;
import com.kuzetech.bigdata.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author kuze
 * @date 2024/8/30 15:47
 */
@Component("TABLE_BUSI_OWNER")
public class TableBusiOwnerAssessor extends Assessor {

    @Override
    public void checkProblem(AssessParam assessParam , GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> BusiOwnerAssessor ");

        //判断是否有业务负责人
        String busiOwnerUserName = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getBusiOwnerUserName();
        if(busiOwnerUserName == null || Constant.BUSI_OWNER_UNSET.equals( busiOwnerUserName )){
            //没有业务负责人
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("未设定业务负责人");
            //考评备注(可选)
            //governanceAssessDetail.setAssessComment();
        }
    }
}
