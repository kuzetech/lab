package com.atguigu.dga.governance.assessor.calc;

import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.atguigu.dga.util.SqlUtil;
import lombok.Getter;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Stack;

/**
 * @author WEIYUNHUI
 * @date 2024/9/3 9:23
 */
@Component("TABLE_SELECT_ALL")
public class TableSelectAllAssessor extends Assessor {
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
        SelectAllDispatcher selectAllDispatcher = new SelectAllDispatcher();

        SqlUtil.parseSql( taskSql , selectAllDispatcher);

        //判断节点处理器中记录的信息
        if(selectAllDispatcher.getIsContainsSelectAll()){
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("SQL中存在select*的操作");
        }

    }

    public static class SelectAllDispatcher implements Dispatcher{

        @Getter
        private Boolean isContainsSelectAll = false  ;

        /**
         * 在遍历的过程中， 每遍历到一个节点，都要调用一次该方法
         */
        @Override
        public Object dispatch(Node nd, Stack<Node> stack, Object... nodeOutputs) throws SemanticException {
            //判断当前节点是否为 select *
            ASTNode astNode = (ASTNode) nd;
            if(astNode.getType() == HiveParser.TOK_ALLCOLREF){
                isContainsSelectAll = true ;
                return null ;
            }
            return null;
        }
    }
}
