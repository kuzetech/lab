package com.atguigu.dga.governance.assessor.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 15:28
 */
@Component("TABLE_SIMILAR")
public class TableSimilarAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableSimilarAssessor ");

        //提取指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramPercent = paramJsonObj.getInteger("percent");

        //提取当前表
        TableMetaInfo tableMetaInfo = assessParam.getTableMetaInfo();
        //提取当前表所属的数仓层级
        String dwLevel = tableMetaInfo.getTableMetaInfoExtra().getDwLevel();

        //提取同层次的所有表, 排除自己
        List<TableMetaInfo> sameLevelList = assessParam.getTableMetaInfoList().stream().filter(
                ti -> dwLevel.equals(ti.getTableMetaInfoExtra().getDwLevel()) && !ti.getTableName().equals(tableMetaInfo.getTableName())
        ).collect(
                Collectors.toList()
        );

        //判断是否存在相似表
        //提取当前表的所有字段
        List<String> currentTableColList = getTableColList(tableMetaInfo);

        //维护相似表
        ArrayList<String> similarTable = new ArrayList<>();

        for (TableMetaInfo otherTableMetaInfo : sameLevelList) {
            //提取其他表的所有字段
            List<String> otherTableColList = getTableColList(otherTableMetaInfo);

            //取交集
            Collection intersection = CollectionUtils.intersection(currentTableColList, otherTableColList);

            if(intersection.size() > 0 ){
                // 相同字段所占百分比
                int realPercent = intersection.size() * 100 / currentTableColList.size();
                if( realPercent > paramPercent){
                    //记录相似表
                    similarTable.add( otherTableMetaInfo.getTableName()) ;
                }
            }
        }

        if(similarTable.size() > 0 ){
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("同层次存在相似表");
            //考评备注
            governanceAssessDetail.setAssessComment("同层次相似表为: " + similarTable );
        }

    }

    private  List<String> getTableColList(TableMetaInfo tableMetaInfo) {
        return JSON.parseArray(tableMetaInfo.getColNameJson(), JSONObject.class).stream().map(
                jsonObj -> jsonObj.getString("name")
        ).collect(
                Collectors.toList()
        );
    }
}
