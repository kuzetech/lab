package com.atguigu.dga.governance.assessor.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.util.HttpUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WEIYUNHUI
 * @date 2024/9/3 14:03
 */
@Component("TABLE_DATA_SKEW")
public class TableDataSkewAssessor extends Assessor {

    @Value("${spark.history.rest.api.url}")
    private String sparkHistoryRestApiUrl ;

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableSelectAllAssessor ");

        //排除ODS的表
        if(DgaConstant.DW_LEVEL_ODS.equals( assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())){
            return ;
        }

        //指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        Integer paramPercent = paramJsonObj.getInteger("percent");
        Integer paramStageDurSeconds = paramJsonObj.getInteger("stage_dur_seconds");

        //提取YarnId
        String yarnId = assessParam.getTDsTaskInstance().getAppLink();

        // 提取 completed = true 的 attemptId
        String completedAttemptId  = getCompletedAttemptId( yarnId );

        // 提取 status = completed 的 stageId
        List<String> completedStageIdList = getCompletedStageIdList(yarnId , completedAttemptId);

        // 将每个阶段中中的所有的task信息，封装到自定义的 MyStage对象中
        List<MyStage> myStageList = getStageList(yarnId , completedAttemptId , completedStageIdList);
        ArrayList<MyStage> skewMyStageList = new ArrayList<>();
        for (MyStage myStage : myStageList) {
            if(myStage.getMaxTaskDuration() > paramStageDurSeconds * 1000 ){
                if(myStage.getMaxTaskDurationPercent() > paramPercent ){
                    skewMyStageList.add( myStage ) ;
                }
            }
        }

        if( skewMyStageList.size() > 0 ){
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("计算中存在数据倾斜");
            //备注
            governanceAssessDetail.setAssessComment("存在倾斜的阶段: " + skewMyStageList);
        }

    }

    /**
     * 将每个阶段中中的所有的task信息，封装到自定义的 MyStage对象中
     * @param yarnId
     * @param completedAttemptId
     * @param completedStageIdList
     * @return
     */
    private List<MyStage> getStageList(String yarnId, String completedAttemptId, List<String> completedStageIdList) {
        ArrayList<MyStage> myStageArrayList = new ArrayList<>(completedStageIdList.size());

        for (String stageId : completedStageIdList) {
            // http://hadoop102:18080/api/v1/applications/{yarnId}/{attemptId}/stages/{stageId}
            // http://hadoop102:18080/api/v1/applications/application_1684083580862_0012/1/stages/2
            //拼接url
            String url = sparkHistoryRestApiUrl + "/" + yarnId + "/" + completedAttemptId + "/stages/" + stageId ;
            String jsonStr = HttpUtil.get(url);
            List<JSONObject> stageJsonArr = JSON.parseArray(jsonStr, JSONObject.class);
            // 提取 status = completed 的 stage ， 因为会有多次尝试， 要取尝试成功的
            JSONObject completedStageJsonObj = null ;
            for (JSONObject stageJsonObj : stageJsonArr) {
                if ("COMPLETE".equals(stageJsonObj.getString("status"))) {
                    completedStageJsonObj = stageJsonObj ;
                    break;
                }
            }

            Long maxTaskDuration = 0L ;
            Long toalTaskDuration = 0L ;

            Long numTask = 0L ;

            // 提取tasks
            JSONObject taskJsonObj = completedStageJsonObj.getJSONObject("tasks");
            for (String key : taskJsonObj.keySet()) {
                JSONObject valueJsonObj = taskJsonObj.getJSONObject(key);
                if("SUCCESS".equals( valueJsonObj.getString("status") ) ){
                    maxTaskDuration = Math.max(maxTaskDuration, valueJsonObj.getLong("duration"));
                    toalTaskDuration += valueJsonObj.getLong("duration");
                    numTask ++ ;
                }
            }

            //封装阶段对象
            MyStage myStage = new MyStage();
            myStage.setStageId( stageId );
            myStage.setMaxTaskDuration( maxTaskDuration );
            Long avgTaskDuration = toalTaskDuration / numTask ;
            myStage.setAvgTaskDuration( avgTaskDuration );
            myStage.setMaxTaskDurationPercent( (maxTaskDuration - avgTaskDuration) * 100 / avgTaskDuration );

            myStageArrayList.add( myStage  ) ;

        }

        return myStageArrayList ;
    }

    /**
     * 提取 status = completed 的 stageId
     * @param yarnId
     * @param completedAttemptId
     * @return
     */
    private List<String> getCompletedStageIdList(String yarnId, String completedAttemptId) {
        // http://hadoop102:18080/api/v1/applications/{yarnId}/{attemptId}/stages
        // http://hadoop102:18080/api/v1/applications/application_1684083580862_0012/1/stages

        String url = sparkHistoryRestApiUrl + "/" + yarnId + "/" + completedAttemptId + "/stages";

        String jsonStr = HttpUtil.get(url);
        List<JSONObject> stageJsonObjList = JSON.parseArray(jsonStr, JSONObject.class);
        List<String> completedStageIdList = stageJsonObjList.stream().filter(
                jsonObj -> "COMPLETE".equals(jsonObj.getString("status"))
        ).map(
                jsonObj -> jsonObj.getString("stageId")
        ).collect(
                Collectors.toList()
        );

        return completedStageIdList ;
    }

    /**
     * 获取完成的attempted
     * @param yarnId
     * @return
     */
    private String getCompletedAttemptId(String yarnId) {
        // http://hadoop102:18080/api/v1/applications/{yarnId}
        // http://hadoop102:18080/api/v1/applications/application_1684083580862_0012
        String url = sparkHistoryRestApiUrl  + "/" + yarnId ;
        String jsonStr = HttpUtil.get(url);
        JSONObject jsonObj = JSON.parseObject(jsonStr);
        //提取 attempts
        JSONArray attemptsJsonArr = jsonObj.getJSONArray("attempts");
        for (int i = 0; i < attemptsJsonArr.size(); i++) {
            JSONObject attemptJsonObj = attemptsJsonArr.getJSONObject(i);
            if( attemptJsonObj.getBoolean("completed")) {
                return attemptJsonObj.getString("attemptId");
            }
        }
        return null ;
    }

    @Data
    class MyStage {
        // 阶段Id
        private String stageId ;

        // 最大任务耗时
        private Long maxTaskDuration ;

        // 平均任务耗时
        private Long avgTaskDuration ;

        // 最大任务耗时超过平均任务耗时百分比
        private Long maxTaskDurationPercent ;
    }
}
