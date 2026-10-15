package com.kuzetech.bigdata.governance.assessor.storage;

import com.kuzetech.bigdata.governance.assessor.Assessor;
import com.kuzetech.bigdata.governance.bean.AssessParam;
import com.kuzetech.bigdata.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author kuze
 * @date 2024/8/30 16:16
 */
@Component("TABLE_EMPTY")
public class TableEmptyAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam , GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> TableEmptyAssessor ");

        //判断表的大小是否为0L
        Long tableSize = assessParam.getTableMetaInfo().getTableSize();
        if(tableSize == 0L){
            //空表
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("空表");
        }
    }
}
