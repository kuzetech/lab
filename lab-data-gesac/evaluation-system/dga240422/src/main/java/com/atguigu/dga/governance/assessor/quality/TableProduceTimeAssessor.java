package com.atguigu.dga.governance.assessor.quality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.governance.bean.TDsTaskInstance;
import com.atguigu.dga.governance.service.TDsTaskInstanceService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/9/2 15:35
 */
@Component("TABLE_PRODUCE_TIME")
public class TableProduceTimeAssessor  extends Assessor {

    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService ;


    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableProduceTimeAssessor ");

        //排除ODS表
        if(DgaConstant.DW_LEVEL_ODS.equals( assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())){
            return ;
        }

        //指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramDays = paramJsonObj.getInteger("days");
        Integer paramPercent = paramJsonObj.getInteger("percent");

        //计算当日产出时效
        TDsTaskInstance currentTaskInstance = assessParam.getTDsTaskInstance();
        Long  currentProduceTime = currentTaskInstance.getEndTime().getTime() - currentTaskInstance.getStartTime().getTime();

        //前days天平均产出时效
        String assessDateStr = assessParam.getAssessDate();
        Date assessDate = DateUtils.parseDate(assessDateStr, "yyyy-MM-dd");

        //计算前days天的开始天
        Date startDate = DateUtils.addDays(assessDate, -paramDays);
        String startDateStr = DateFormatUtils.format(startDate, "yyyy-MM-dd");
        String name = assessParam.getTableMetaInfo().getSchemaName() + "." + assessParam.getTableMetaInfo().getTableName();
        List<TDsTaskInstance> beforeNDaysInstanceList =
                tDsTaskInstanceService.getBeforeNDaysTaskInstanceList( name  , startDateStr , assessDateStr );

        //计算前days天的平均产出时效
        if(beforeNDaysInstanceList.size() > 0 ){
            Long sumTime = 0L ;
            for (TDsTaskInstance tDsTaskInstance : beforeNDaysInstanceList) {
                Long  produceTime = tDsTaskInstance.getEndTime().getTime() - tDsTaskInstance.getStartTime().getTime();
                sumTime += produceTime ;
            }

            Long  avgProduceTime = sumTime / beforeNDaysInstanceList.size();

            // 判断当日是否超过前days天的平均产出时效的 percent
            if(  ( currentProduceTime - avgProduceTime ) * 100 / avgProduceTime  > paramPercent ){
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem("产出时效异常");
                //备注
                governanceAssessDetail.setAssessComment("当日产出时效: " + currentProduceTime +  " , 前 " + beforeNDaysInstanceList.size() + " 天的平均产出时效: " + avgProduceTime);
            }
        }
    }
}
