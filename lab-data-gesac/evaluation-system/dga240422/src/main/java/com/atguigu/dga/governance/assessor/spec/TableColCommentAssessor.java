package com.atguigu.dga.governance.assessor.spec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 11:25
 */
@Component("TABLE_COL_COMMENT")
public class TableColCommentAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) {
        System.out.println("开始考评 ==> TableColCommentAssessor ");

        //提取表的所有字段
        String colNameJson = assessParam.getTableMetaInfo().getColNameJson();
        //转换成Json对象集合
        List<JSONObject> colList = JSON.parseArray(colNameJson, JSONObject.class);
        //提取没有备注的字段
        List<String> missCommentColList = colList.stream().filter(jsonObj -> jsonObj.getString("comment") == null || jsonObj.getString("comment").trim().isEmpty())
                .map(jsonObj -> jsonObj.getString("name"))
                .collect(Collectors.toList());
        if (missCommentColList.size() > 0) {
            //有没有备注的字段
            //给分
            governanceAssessDetail.setAssessScore( BigDecimal.valueOf((colList.size() - missCommentColList.size()) * 10L  / colList.size() ) );
            //问题项
            governanceAssessDetail.setAssessProblem("存在无备注字段");
            //考评备注
            governanceAssessDetail.setAssessComment("无备注字段: " + missCommentColList );
        }
    }
}
