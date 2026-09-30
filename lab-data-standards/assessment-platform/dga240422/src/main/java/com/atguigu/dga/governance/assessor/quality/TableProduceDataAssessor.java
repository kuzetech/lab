package com.atguigu.dga.governance.assessor.quality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;

/**
 * @author WEIYUNHUI
 * @date 2024/9/2 9:03
 */
@Component("TABLE_PRODUCE_DATA")
public class TableProduceDataAssessor  extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableProduceDataAssessor ");

        //判断是否为日分区表
        if(!DgaConstant.LIFECYCLE_TYPE_DAY.equals(assessParam.getTableMetaInfo().getTableMetaInfoExtra().getLifecycleType())){
            return ;
        }

        //指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramDays = paramJsonObj.getInteger("days");
        Integer paramUpperLimit = paramJsonObj.getInteger("upper_limit");
        Integer paramLowerLimit = paramJsonObj.getInteger("lower_limit");

        //表在hdfs的路径
        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath();
        //获取文件系统对象
        FileSystem fs = FileSystem.get(new URI(tableFsPath), new Configuration(), assessParam.getTableMetaInfo().getTableFsOwner());
        //提取分区字段
        String partitionCol = JSON.parseArray(assessParam.getTableMetaInfo().getPartitionColNameJson(), JSONObject.class).get(0).getString("name");
        //处理当日分区路径（考评日期-1）
        String assessDateStr = assessParam.getAssessDate();
        Date assessDate = DateUtils.parseDate(assessDateStr, "yyyy-MM-dd");
        Date currentPartitionDate = DateUtils.addDays(assessDate, -1);
        String currentPartitionStr = DateFormatUtils.format(currentPartitionDate, "yyyy-MM-dd");
        String currentPath = tableFsPath + "/" +partitionCol + "=" + currentPartitionStr ;

        //计算出当日产出数据量大小
        //数仓中只有一级分区，可以直接将分区路径处理好，直接汇总分区下的数据量大小，
        //如果有多级分区， 要通过递归的方式汇总数据量大小

        Long currentProduceDataSize  = calcPartitionDataSize(fs, currentPath);

        //计算出前 days天产出数据量总大小，并计算平均产出数据量
        Long beforeNTotalDataSize = 0L ;
        Long realBeforeDay = 0L;
        for (int i = 1; i <= paramDays; i++) {
            Date beforeNPartitionDate = DateUtils.addDays(currentPartitionDate, -i);
            String beforeNPartitionStr = DateFormatUtils.format(beforeNPartitionDate, "yyyy-MM-dd");
            String beforeNPath  = tableFsPath + "/" +partitionCol + "=" + beforeNPartitionStr ;
            Long beforeNDataSize  = calcPartitionDataSize(fs, beforeNPath);
            if(beforeNDataSize != null ){
                beforeNTotalDataSize += beforeNDataSize ;
                realBeforeDay ++ ;
            }
        }
        if(realBeforeDay > 0L ){
            Long  beforeNAvgDataSize = beforeNTotalDataSize / realBeforeDay;

            //判断是否越界
            //比较是否超过上限
            if (( currentProduceDataSize  - beforeNAvgDataSize ) * 100 /  beforeNAvgDataSize > paramUpperLimit) {
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem("产出数据量异常");
                //备注
                governanceAssessDetail.setAssessComment("当日产出数据量超过前 " + realBeforeDay + " 平均产出数据量的 " + paramUpperLimit + " %");
                return ;
            }

            //比较是否低于下限
            if( currentProduceDataSize * 100  / beforeNAvgDataSize  < paramLowerLimit){
                //给分
                governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
                //问题项
                governanceAssessDetail.setAssessProblem("产出数据量异常");
                //备注
                governanceAssessDetail.setAssessComment("当日产出数据量低于前 " + realBeforeDay + " 平均产出数据量的 " + paramLowerLimit + " %");

            }
        }
    }

    private static Long  calcPartitionDataSize(FileSystem fs, String currentPath ) throws IOException {
        //判断当前分区是否存在
        if( !fs.exists( new Path( currentPath )) ) {
            return null ;
        }

        Long currentProduceDataSize = 0L ;
        FileStatus[] currentFileStatuses = fs.listStatus(new Path(currentPath));
        for (FileStatus currentFileStatus : currentFileStatuses) {
            currentProduceDataSize += currentFileStatus.getLen() ;
        }
        return currentProduceDataSize ;
    }
}
