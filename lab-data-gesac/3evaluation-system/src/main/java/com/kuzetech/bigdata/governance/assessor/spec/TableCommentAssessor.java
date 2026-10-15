package com.kuzetech.bigdata.governance.assessor.spec;

import com.kuzetech.bigdata.governance.assessor.Assessor;
import com.kuzetech.bigdata.governance.bean.AssessParam;
import com.kuzetech.bigdata.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author kuze
 * @date 2024/8/30 16:14
 */
@Component("TABLE_COMMENT")
public class TableCommentAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam , GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> TableCommentAssessor ");

        //判断表是否有备注信息
        String tableComment = assessParam.getTableMetaInfo().getTableComment();
        if(tableComment == null || tableComment.trim().isEmpty()){
            //没有备注信息
            //给分
            governanceAssessDetail.setAssessScore( BigDecimal.ZERO );
            //问题项
            governanceAssessDetail.setAssessProblem("表没有备注信息");
        }

    }
}
