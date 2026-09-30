package com.atguigu.dga.governance.assessor.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 11:39
 */
@Component("TABLE_NO_ACCESS")
public class TableNoAccessAssessor  extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail)  throws Exception{
        System.out.println("开始考评 ==> TableNoAccessAssessor ");

        //提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramDays = paramJsonObj.getInteger("days");

        //提取表的最后访问时间
        Date tableLastAccessTime = assessParam.getTableMetaInfo().getTableLastAccessTime();
        //截断到天 yyyy-MM-dd
        Date tableLastAccessDate = DateUtils.truncate(tableLastAccessTime, Calendar.DAY_OF_MONTH);
        //转换成毫秒值
        long tableLastAccessMs = tableLastAccessDate.getTime();

        //当前考评日期
        String assessDateStr = assessParam.getAssessDate();
        //转换成日期
        Date assessDate = DateUtils.parseDate(assessDateStr, "yyyy-MM-dd");
        //转换成毫秒值
        long assessMs = assessDate.getTime();

        //考评日期 减去 表最后的访问时间
        long diffMs = assessMs - tableLastAccessMs;
        if( diffMs > 0 ){
            //转换成天
            long diffDays = TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS);
            if ( diffDays > paramDays ){
                // 长期无访问
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem(" 超过 " + paramDays + " 天无访问");
            }
            //考评备注
            governanceAssessDetail.setAssessComment(" 实际超过 " + diffDays + " 天无访问");
        }
    }
}
