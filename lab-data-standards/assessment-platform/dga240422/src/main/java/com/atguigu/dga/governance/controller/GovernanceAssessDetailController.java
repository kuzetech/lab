package com.atguigu.dga.governance.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.governance.bean.GovernanceAssessGlobal;
import com.atguigu.dga.governance.bean.GovernanceAssessTecOwner;
import com.atguigu.dga.governance.service.GovernanceAssessDetailService;
import com.atguigu.dga.governance.service.GovernanceAssessGlobalService;
import com.atguigu.dga.governance.service.GovernanceAssessTecOwnerService;
import com.atguigu.dga.governance.service.MainService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 治理考评结果明细 前端控制器
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-30
 */
@RestController
@RequestMapping("/governance")
public class GovernanceAssessDetailController {

    @Autowired
    GovernanceAssessGlobalService governanceAssessGlobalService ;

    @Autowired
    GovernanceAssessTecOwnerService governanceAssessTecOwnerService;

    @Autowired
    GovernanceAssessDetailService governanceAssessDetailService;

    @Autowired
    MainService mainService;

    /**
     * 重新评估接口
     *
     * 接口路径: /governance/assess/{date}
     *
     * 接口请求方式: POST
     *
     * 接口参数: {date}
     *
     * 接口返回值: success
     *
     */

    @PostMapping("/assess/{date}")
    public String assess( @PathVariable("date") String assessDate ){
        mainService.startGovernanceAssess(assessDate);

        return "success" ;
    }

    /**
     * 问题列表接口
     *
     * 接口路径: /governance/problemList/{governType}/{pageNo}/{pageSize}
     *
     * 接口请求方式: GET
     *
     * 接口参数: {governType}   治理类型
     *         {pageNo}       页码
     *         {pageSize}     每页显示条数
     *
     * 接口返回值: [
     *            {"assessComment":"","assessDate":"2023-05-01","assessProblem":"缺少技术OWNER","assessScore":0.00,"commentLog":"","createTime":1682954933000,"governanceType":"SPEC","governanceUrl":"/table_meta/table_meta/detail?tableId=1803","id":21947,"isAssessException":"0","metricId":1,"metricName":"是否有技术Owner","schemaName":"gmall","tableName":"ads_page_path"}
     *            ,
     *            {"assessComment":"","assessDate":"2023-05-01","assessProblem":"缺少业务OWNER","assessScore":0.00,"commentLog":"","createTime":1682954933000,"governanceType":"SPEC","governanceUrl":"/table_meta/table_meta/detail?tableId=1803","id":21948,"isAssessException":"0","metricId":2,"metricName":"是否有业务Owner","schemaName":"gmall","tableName":"ads_page_path"}
     *            ,
     *            {"assessComment":"","assessDate":"2023-05-01","assessProblem":"缺少技术OWNER","assessScore":0.00,"commentLog":"","createTime":1682954933000,"governanceType":"SPEC","governanceUrl":"/table_meta/table_meta/detail?tableId=1804","id":21964,"isAssessException":"0","metricId":1,"metricName":"是否有技术Owner","schemaName":"gmall","tableName":"ads_user_change"}
     *            ,
     *            ...
     *           ]

     */
    @GetMapping("/problemList/{governType}/{pageNo}/{pageSize}")
    public String problemList(@PathVariable("governType") String governType ,
                              @PathVariable("pageNo") Integer pageNo ,
                              @PathVariable("pageSize") Integer pageSize ){

        //计算分页开始行
        Integer start  = ( pageNo - 1) * pageSize ;

        List<GovernanceAssessDetail> governanceAssessDetailList = governanceAssessDetailService.list(
                new QueryWrapper<GovernanceAssessDetail>()
                        .eq("governance_type", governType)
                        .lt("assess_score", 10)
                        .inSql("assess_date", "SELECT MAX(assess_date) FROM governance_assess_detail")
                        .last("limit " + start + " , " + pageSize)
        );

        return JSON.toJSONString( governanceAssessDetailList ) ;

    }



    /**
     * 问题项个数接口
     *
     * 接口路径: /governance/problemNum
     *
     * 接口请求方式: GET
     *
     * 接口参数:  无
     *
     * 接口返回值: {"SPEC":1, "STORAGE":4,"CALC":12,"QUALITY":34,"SECURITY":12}
     */

    @GetMapping("/problemNum")
    public String problemNum(){
        List<Map<String, Object>> mapList = governanceAssessDetailService.listMaps(
                new QueryWrapper<GovernanceAssessDetail>()
                        .select("governance_type", "SUM( IF(assess_score < 10 , 1 , 0 )) as  problem_num")
                        .inSql("assess_date", "SELECT MAX(assess_date) FROM governance_assess_detail")
                        .groupBy("governance_type")
        );

        JSONObject resultJsonObj = new JSONObject();

        mapList.forEach(
                map -> resultJsonObj.put( map.get("governance_type").toString() , map.get("problem_num"))
        );

        return resultJsonObj.toJSONString();
    }

    /**
     * 分组人员排行榜接口
     *
     * 接口路径: /governance/rankList
     *
     * 接口请求方式: GET
     *
     * 接口参数:  无
     *
     * 接口返回值:
     * [
     *  {"tecOwner":"zhang3" ,"score":99},
     *  {"tecOwner":"li4" ,"score":98},
     *  {"tecOwner": "wang5","score":97}
     * ]
     */

    @GetMapping("/rankList")
    public String rankList(){
        //查询人级分数
        List<Map<String, Object>> mapList = governanceAssessTecOwnerService.listMaps(
                new QueryWrapper<GovernanceAssessTecOwner>()
                        .select("tec_owner as tecOwner", "score")
                        .inSql("assess_date", " select max(assess_date) from governance_assess_tec_owner")
                        .orderByDesc("score")
        );

        return JSON.toJSONString( mapList ) ;
    }



    /**
     * 全局分数接口
     *
     * 接口路径: /governance/globalScore
     *
     * 接口请求方式: GET
     *
     * 接口参数: 无
     *
     * 接口返回值: {  "assessDate":"2023-04-01" ,"sumScore":90, "scoreList":[20,40,34,55,66] }
     */
    @GetMapping("/globalScore")
    public String globalScore(){
        GovernanceAssessGlobal governanceAssessGlobal = governanceAssessGlobalService.getOne(
                new QueryWrapper<GovernanceAssessGlobal>()
                        .inSql("assess_date", " select max(assess_date) from governance_assess_global")
        );

        //封装结果
        JSONObject resultJsonObj = new JSONObject();
        resultJsonObj.put("assessDate" , governanceAssessGlobal.getAssessDate()) ;
        resultJsonObj.put("sumScore" , governanceAssessGlobal.getScore()) ;
        List<BigDecimal> scoreList = Arrays.asList(
                governanceAssessGlobal.getScoreSpec(),
                governanceAssessGlobal.getScoreStorage(),
                governanceAssessGlobal.getScoreCalc(),
                governanceAssessGlobal.getScoreQuality(),
                governanceAssessGlobal.getScoreSecurity()
        );
        resultJsonObj.put("scoreList" , scoreList) ;

        return resultJsonObj.toJSONString() ;

    }
}
