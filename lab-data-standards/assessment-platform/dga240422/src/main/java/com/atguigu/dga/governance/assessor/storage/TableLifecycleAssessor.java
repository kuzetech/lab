package com.atguigu.dga.governance.assessor.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 15:11
 */
@Component("TABLE_LIFECYCLE")
public class TableLifecycleAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableLifecycleAssessor ");

        //提取表的生命周期类型
        String lifecycleType = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleType();

        //是否设置过生命周期类型
        if(lifecycleType == null || DgaConstant.LIFECYCLE_TYPE_UNSET.equals(lifecycleType)){
            //没有设定生命周期类型
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("表生命周期类型未设定");
            return ;
        }

        //判断生命周期类型
        if(DgaConstant.LIFECYCLE_TYPE_DAY.equals(lifecycleType)){
            //日分区类型
            //提取表的分区信息
            String partitionColNameJson = assessParam.getTableMetaInfo().getPartitionColNameJson();
            if(partitionColNameJson == null || partitionColNameJson.trim().isEmpty()){
                //没有分区信息
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem("日分区表无分区信息");
                return ;
            }

            //判断日分区表生命周期天数
            Long lifecycleDays = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleDays();
            if(lifecycleDays == null || lifecycleDays == -1L ){
                //生命周期天数未设定
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem("日分区表生命周期天数未设定");
                return ;
            }

            //判断日分区表生命周期天数  是否 超过指标的建议值
            //提取指标参数
            String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
            JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
            Integer paramDays = paramJsonObj.getInteger("days");

            if(lifecycleDays > paramDays ){
                //给分
                governanceAssessDetail.setAssessScore( BigDecimal.valueOf( paramDays * 10  / lifecycleDays ));
                //问题项
                governanceAssessDetail.setAssessProblem("日分区表生命周期天数超过建议值");
                //考评备注
                governanceAssessDetail.setAssessComment("日分区建议生命周期天数: " + paramDays + " , 实际设置的天数: " + lifecycleDays);
            }

        }








    }
}
