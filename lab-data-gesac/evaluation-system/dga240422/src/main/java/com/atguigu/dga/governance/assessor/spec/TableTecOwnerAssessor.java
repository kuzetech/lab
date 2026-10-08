package com.atguigu.dga.governance.assessor.spec;

import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author WEIYUNHUI
 * @date 2024/8/30 15:41
 */

//TABLE_TEC_OWNER
@Component("TABLE_TEC_OWNER")
public class TableTecOwnerAssessor extends Assessor {

    @Override
    public void checkProblem(AssessParam assessParam , GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> TecOwnerAssessor ");

        /*if("ods_log_inc".equals(assessParam.getTableMetaInfo().getTableName())){
            throw new RuntimeException("模拟的异常");
        }*/

        //判断是否有技术负责人
        String tecOwnerUserName = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getTecOwnerUserName();
        if(tecOwnerUserName == null || DgaConstant.TEC_OWNER_UNSET.equals( tecOwnerUserName )){
            //没有技术负责人
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("未设定技术负责人");
            //考评备注(可选)
            //governanceAssessDetail.setAssessComment();
        }
    }
}
