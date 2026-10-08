package com.atguigu.dga.governance.assessor.spec;

import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 14:07
 *
 * 正则表达式:
 *   ^ :  匹配输入字符串的开始位置。
 *   $ :  匹配输入字符串的结束位置。
 *   * :  表示 0-n次
 *   + :  表示 1-n次
 *   ? :  表示 0-1次
 *   {}:  表示次数 , {n}表示n次 ,  {n,m} 表示n到m次  ,  {n,} 表示n次以上
 *   [] : 匹配字符 , [abc] 表示匹配abc中的任意字符 ， [123]表示匹配123中的任意字符 ， [a-zA-Z0-9] 表示匹配数字与字母
 *   \d : 匹配数字， 等价于 [0-9]
 *   \w : 匹配字母数字下划线 ， 等价于 [a-zA-Z0-9_]
 *   () : 匹配字符串，  (abc) 表示匹配abc完整字符串
 *   |  : 或 ， (abc|xyz) 表示匹配abc 或者 xyz完整字符串
 *   .  : 任意字符
 */
@Component("TABLE_NAME_STANDARD")
public class TableNameStandardAssesor extends Assessor {

    Pattern odsPattern = Pattern.compile("^ods_\\w+_(inc|full)$");
    Pattern dimPattern = Pattern.compile("^dim_\\w+(_(zip|full))?$");
    Pattern dwdPattern = Pattern.compile("^dwd_\\w+_\\w+_(inc|full|acc)$");
    Pattern dwsPattern = Pattern.compile("^dws_\\w+_\\w+_\\w+_(1d|nd|td)$");
    Pattern adsPattern = Pattern.compile("^ads_\\w+$");
    Pattern dmPattern = Pattern.compile("^dm_\\w+$");


    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableNoProduceAssessor ");

        //提取表的名字
        String tableName = assessParam.getTableMetaInfo().getTableName();
        //表所属的数仓层级
        String dwLevel = assessParam.getTableMetaInfo().getTableMetaInfoExtra().getDwLevel();
        Matcher matcher = null ;
        if(DgaConstant.DW_LEVEL_ODS.equals(dwLevel)){
             matcher = odsPattern.matcher(tableName);
        }else if(DgaConstant.DW_LEVEL_DIM.equals(dwLevel)){
             matcher = dimPattern.matcher(tableName);
        }else if(DgaConstant.DW_LEVEL_DWD.equals(dwLevel)){
             matcher = dwdPattern.matcher(tableName);
        }else if(DgaConstant.DW_LEVEL_DWS.equals(dwLevel)){
             matcher = dwsPattern.matcher(tableName);
        }else if(DgaConstant.DW_LEVEL_ADS.equals(dwLevel)){
             matcher = adsPattern.matcher(tableName);
        }else if(DgaConstant.DW_LEVEL_DM.equals(dwLevel)){
             matcher = dmPattern.matcher(tableName) ;
        }else{
            //DgaConstant.DW_LEVEL_OTHER
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.valueOf(5L));
            //问题项
            governanceAssessDetail.setAssessProblem("未纳入分层");
        }

        if(matcher != null && !matcher.matches()){
            //给分
            governanceAssessDetail.setAssessScore(BigDecimal.ZERO);
            //问题项
            governanceAssessDetail.setAssessProblem("表名不符合规范");
        }

    }

    public static void main(String[] args) {
        // 匹配邮箱
        //邮箱地址
        String email = "zhangsan@atguigu.xyz";
        //正则
        String regex = "^\\w{6,15}@[a-z0-9]{2,8}\\.(com|cn|vip)$" ;
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(email);

        System.out.println(matcher.matches());

    }
}
