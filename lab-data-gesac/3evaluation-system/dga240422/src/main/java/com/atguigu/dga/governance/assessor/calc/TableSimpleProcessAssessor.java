package com.atguigu.dga.governance.assessor.calc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.util.SqlUtil;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.parquet.io.ValidatingRecordConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WEIYUNHUI
 * @date 2024/9/3 10:03
 */
@Component("TABLE_SIMPLE_PROCESS")
public class TableSimpleProcessAssessor extends Assessor {

    @Value("${default.dw.name}")
    private String defaultDwName ;

    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableSelectAllAssessor ");

        //排除ODS的表
        if(DgaConstant.DW_LEVEL_ODS.equals( assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel())){
            return ;
        }

        //提取SQL
        String taskSql = assessParam.getTDsTaskDefinition().getTaskSql();

        //解析SQL
        SimpleProcessDispatcher simpleProcessDispatcher = new SimpleProcessDispatcher( defaultDwName );
        SqlUtil.parseSql( taskSql , simpleProcessDispatcher );

        // 判断是否有复杂计算
        Set<String> sqlComplicateSet = simpleProcessDispatcher.getSqlComplicateSet();
        if(sqlComplicateSet.size() > 0 ){
            //考评备注
            governanceAssessDetail.setAssessComment("Sql中实际存在的复杂计算: " + sqlComplicateSet );
            return ;
        }
        // where后面的过滤字段
        Set<String> sqlFilterSet = simpleProcessDispatcher.getSqlFilterSet();

        // 被查询表的分区字段
        Set<String> sqlRefTable = simpleProcessDispatcher.getSqlRefTable();
        HashMap<String, TableMetaInfo> tableMetaInfoHashMap = new HashMap<>();
        assessParam.getTableMetaInfoList().forEach(
                tableMetaInfo -> tableMetaInfoHashMap.put( tableMetaInfo.getSchemaName()+ "." + tableMetaInfo.getTableName() , tableMetaInfo)
        );

        HashSet<String> allPartitionSet = new HashSet<>();
        for (String tableName : sqlRefTable) {
            TableMetaInfo tableMetaInfo = tableMetaInfoHashMap.get(tableName);
            //提取分区字段
            List<String> partitionColList = JSON.parseArray(tableMetaInfo.getPartitionColNameJson(), JSONObject.class).stream().map(jsonObj -> jsonObj.getString("name")).collect(
                    Collectors.toList()
            );
            allPartitionSet.addAll( partitionColList) ;
        }


        //两个集合差集计算
        Collection subtract = CollectionUtils.subtract(sqlFilterSet, allPartitionSet);

        if(subtract.size() > 0 ){
            //where后面的过滤字段由非分区字段
            //给备注
            governanceAssessDetail.setAssessComment("非分区字段过滤: " + subtract );

            return ;
        }

        // 简单加工
        governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
        // 问题项
        governanceAssessDetail.setAssessProblem("SQL为简单加工" );
        // 备注
        governanceAssessDetail.setAssessComment("SQL : " + taskSql);

    }

    public static class SimpleProcessDispatcher implements Dispatcher{

        private String defaultDwName ;
        public SimpleProcessDispatcher(String defaultDwName){
            this.defaultDwName = defaultDwName ;
        }

        //定义一个集合， 哪些计算是复杂计算
        Set<Integer> complicateTokSet = Sets.newHashSet(
                HiveParser.TOK_JOIN ,   // join ,包含通过where连接的情况
                HiveParser.TOK_GROUPBY , // group by
                HiveParser.TOK_LEFTOUTERJOIN , // left join
                HiveParser.TOK_RIGHTOUTERJOIN , //right join
                HiveParser.TOK_FULLOUTERJOIN , // full join
                HiveParser.TOK_FUNCTION , // count(1)
                HiveParser.TOK_FUNCTIONDI, // count(distinct xx)
                HiveParser.TOK_FUNCTIONSTAR , // count(*)
                HiveParser.TOK_SELECTDI , // distinct
                HiveParser.TOK_UNIONALL // union
        ) ;

        //定义集合，保存sql中实际存在的复杂计算
        @Getter
        Set<String>  sqlComplicateSet = new  HashSet<String>();

        //定义集合，哪些是where后面条件的操作符号
        Set<Integer> operatorSet = Sets.newHashSet(
                HiveParser.EQUAL ,   // =
                HiveParser.GREATERTHAN , // >
                HiveParser.LESSTHAN, // <
                HiveParser.GREATERTHANOREQUALTO , // >=
                HiveParser.LESSTHANOREQUALTO , // <=
                HiveParser.NOTEQUAL , // <>
                HiveParser.KW_LIKE // like
        ) ;

        //定义集合，维护sql中where后面的过滤字段
        @Getter
        Set<String>  sqlFilterSet = new  HashSet<String>();


        //定义集合， 维护sql中所有被查询的表
        @Getter
        Set<String>  sqlRefTable = new  HashSet<String>();

        @Override
        public Object dispatch(Node nd, Stack<Node> stack, Object... nodeOutputs) throws SemanticException {
            ASTNode astNode = (ASTNode) nd;
            // 找复杂计算
            if(complicateTokSet.contains( astNode.getType() ) ){
                sqlComplicateSet.add( astNode.getText() ) ;
            }

            // where后面的过滤字段
            if(operatorSet.contains( astNode.getType() )  && astNode.getAncestor(HiveParser.TOK_WHERE)!=null  ){
                // t1.dt = xxx
                if( astNode.getChild(0).getType() == HiveParser.DOT ){
                    // .的第二个孩子就是字段
                    String filterCol = astNode.getChild(0).getChild(1).getText();
                    sqlFilterSet.add( filterCol );
                }else{
                    // dt = xxx
                    String filterCol = astNode.getChild(0).getChild(0).getText();
                    sqlFilterSet.add(filterCol) ;
                }
            }

            // 被查询表
            if( astNode.getType() == HiveParser.TOK_TABNAME && astNode.getAncestor(HiveParser.TOK_FROM) != null ){

                if( astNode.getChildren().size() == 1 ){
                    // table
                    String refTableName = astNode.getChild(0).getText();
                    String fullTableName = defaultDwName + "." + refTableName ;

                    sqlRefTable.add( fullTableName ) ;
                }else {
                    // db.table
                    String refDbName = astNode.getChild(0).getText();
                    String refTableName = astNode.getChild(1).getText();
                    String fullTableName = refDbName + "." + refTableName;
                    sqlRefTable.add(fullTableName);
                }

            }
            return null;
        }
    }
}
