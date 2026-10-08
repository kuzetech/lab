package com.atguigu.dga.util;

import com.atguigu.dga.governance.assessor.calc.TableSimpleProcessAssessor;
import org.apache.hadoop.hive.ql.lib.DefaultGraphWalker;
import org.apache.hadoop.hive.ql.lib.Dispatcher;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;

import java.util.Collections;

public class SqlUtil {

    /**
     * 将指定的Sql语句转换成 AST , 并对AST进行遍历
     * @param sql
     * @param dispatcher  节点处理器， 遍历到每个节点，对节点进行一些处理操作
     */
    public static void parseSql(String sql  , Dispatcher dispatcher ){
        try {
            //将sql转换成语法树
            ParseDriver parseDriver = new ParseDriver();
            ASTNode astNode = parseDriver.parse(sql);

            //将TOK_QUERY处理成根节点
            while( astNode.getType() != HiveParser.TOK_QUERY){
                astNode = (ASTNode) astNode.getChild(0) ;
            }
            System.out.println(astNode);

            //遍历
            DefaultGraphWalker walker = new DefaultGraphWalker(dispatcher);
            walker.startWalking(Collections.singleton( astNode ) , null );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String sql = " select a , b , cc(c) from t1 , test.t2  where t1.id = t2.id and dt = xxx  " ;

        TableSimpleProcessAssessor.SimpleProcessDispatcher dispatcher = new TableSimpleProcessAssessor.SimpleProcessDispatcher("gmall");
        parseSql(sql , dispatcher);

        System.out.println("-------");
    }

    
    public static String filterUnsafeSql(String input) {
        if (input == null) {
            return null;
        }

        // 替换 MySQL 中可能导致 SQL 注入的特殊字符
        return input.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\u001A", "\\Z")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
