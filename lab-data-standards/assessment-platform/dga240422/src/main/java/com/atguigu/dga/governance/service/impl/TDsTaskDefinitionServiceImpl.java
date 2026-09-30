package com.atguigu.dga.governance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.bean.TDsTaskDefinition;
import com.atguigu.dga.governance.mapper.TDsTaskDefinitionMapper;
import com.atguigu.dga.governance.service.TDsTaskDefinitionService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-02
 */
@Service
@DS("dolphinscheduler")
public class TDsTaskDefinitionServiceImpl extends ServiceImpl<TDsTaskDefinitionMapper, TDsTaskDefinition> implements TDsTaskDefinitionService {

    @Override
    public List<TDsTaskDefinition> getTDsTaskDefinitionList() {
        List<TDsTaskDefinition> tDsTaskDefinitionList = list(
                new QueryWrapper<TDsTaskDefinition>()
                        .eq("task_type", DgaConstant.DS_TASK_TYPE_SHELL)
        );

        // 提取SQL
        for (TDsTaskDefinition tDsTaskDefinition : tDsTaskDefinitionList) {
            extractSql(tDsTaskDefinition);
        }


        return tDsTaskDefinitionList ;
    }

    /**
     * 从任务定义的参数中将SQL提取出来
     *
     */
    private void extractSql(TDsTaskDefinition tDsTaskDefinition) {
        //从任务定义中取taskParams
        String taskParams = tDsTaskDefinition.getTaskParams();
        //从taskParams取rawScript
        JSONObject taskParamJsonObj = JSON.parseObject(taskParams);
        String rawScript = taskParamJsonObj.getString("rawScript");

        //开始位置：
        //优先找with ， 如果有就用， 没有就找 insert
        int startIndex = -1 ;
        int withIndex = rawScript.indexOf("with");
        if( withIndex == -1 ){
            //找insert
            startIndex = rawScript.indexOf("insert");
        }else{
            startIndex = withIndex ;
        }

        //如果没有开始位置，表示任务中没有定义SQL
        if(startIndex == -1){
            return ;
        }

        //结束位置：
        // 从开始位置往后优先找 ; , 如果有就用， 没有就找 "
        int endIndex = -1 ;
        int fenHaoIndex = rawScript.indexOf(";", startIndex);
        if(fenHaoIndex == -1){
            //找 " 的位置
            endIndex =  rawScript.indexOf( "\"" , startIndex);
        }else{
            endIndex = fenHaoIndex ;
        }

        //截取SQL
        String taskSql = rawScript.substring(startIndex, endIndex);

        //补充到任务定义中
        tDsTaskDefinition.setTaskSql( taskSql );
    }
}
