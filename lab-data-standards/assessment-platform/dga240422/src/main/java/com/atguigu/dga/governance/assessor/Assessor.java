package com.atguigu.dga.governance.assessor;

import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author WEIYUNHUI
 * @date 2024/8/30 15:48
 *
 *
 *
 * 考评器父类 ， 统一控制考评的流程
 *
 * 采用了模版设计模式
 */
public abstract class Assessor {

    /**
     * 标准的考评流程
     */
    public final GovernanceAssessDetail doAssess(AssessParam assessParam ){

        // 初始化结果对象 GovernanceAssessDetail
        GovernanceAssessDetail governanceAssessDetail = new GovernanceAssessDetail();
        // 将能给的信息提前给
        governanceAssessDetail.setAssessDate( assessParam.getAssessDate() );
        governanceAssessDetail.setTableName( assessParam.getTableMetaInfo().getTableName() );
        governanceAssessDetail.setSchemaName( assessParam.getTableMetaInfo().getSchemaName());
        governanceAssessDetail.setMetricId( assessParam.getGovernanceMetric().getId().toString());
        governanceAssessDetail.setMetricName( assessParam.getGovernanceMetric().getMetricName());
        governanceAssessDetail.setGovernanceType( assessParam.getGovernanceMetric().getGovernanceType());
        governanceAssessDetail.setTecOwner( assessParam.getTableMetaInfo().getTableMetaInfoExtra().getTecOwnerUserName());
        // 考评分数  、 考评问题 、 考评备注  ，需要在查找问题的过程中给定
        // 先给满分，再查找问题的过程中，如果出现问题，在扣分（重新给分）
        governanceAssessDetail.setAssessScore(BigDecimal.TEN);

        try {
            // 查找问题
            checkProblem( assessParam , governanceAssessDetail);
        }catch ( Exception e ){
            //重新给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);

            //将异常信息打印到控制台（主要为了方便看， 或者告诉咱们出现异常了）
            e.printStackTrace();
            //表示出现异常
            governanceAssessDetail.setIsAssessException("1");
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            //提取流中的信息
            String exMsg = stringWriter.toString();
            governanceAssessDetail.setAssessExceptionMsg( exMsg.substring(0 , Math.min(exMsg.length() , 2000)) ) ;
        }

        //治理链接
        // 判断分数是否小于10分， 小于10分， 表示有问题
        if (governanceAssessDetail.getAssessScore().longValue() < 10L) {
            //判断当前指标是否有治理链接
            // /table_meta/table_meta/detail?tableId={tableId}
            String governanceUrl = assessParam.getGovernanceMetric().getGovernanceUrl();
            if( governanceUrl != null ){
                // 提取表的ID
                Long tableId = assessParam.getTableMetaInfo().getId();
                // 替换governanceUrl中的 {tableId}
                governanceUrl = governanceUrl.replace("{tableId}", tableId.toString());
                // 给到结果中
                governanceAssessDetail.setGovernanceUrl( governanceUrl ) ;
            }
        }

        governanceAssessDetail.setCreateTime( new Date( ) );
        // 返回结果
        return governanceAssessDetail ;

    }

    public abstract void checkProblem( AssessParam assessParam , GovernanceAssessDetail governanceAssessDetail ) throws Exception;
}
