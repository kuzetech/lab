package com.atguigu.dga.governance.assessor.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.AssessParam;
import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/9/2 10:15
 */
@Component("TABLE_DIR_FILE_PERMISSION")
public class TableDirFilePermissionAssessor extends Assessor {
    @Override
    public void checkProblem(AssessParam assessParam, GovernanceAssessDetail governanceAssessDetail) throws Exception {
        System.out.println("开始考评 ==> TableDirFilePermissionAssessor ");

        //指标参数
        String metricParamsJson = assessParam.getGovernanceMetric().getMetricParamsJson();
        JSONObject paramJsonObj = JSON.parseObject(metricParamsJson);
        String paramFilePermission = paramJsonObj.getString("file_permission");
        String paramDirPermission = paramJsonObj.getString("dir_permission");

        //判断表目录 以及 子目录 和 文件的权限是否越权
        //表路径
        String tableFsPath = assessParam.getTableMetaInfo().getTableFsPath() ;

        //文件系统对象
        FileSystem fs = FileSystem.get(new URI(tableFsPath), new Configuration(), assessParam.getTableMetaInfo().getTableFsOwner());

        //记录越权的文件 、 目录
        List<String> beyondFileList = new ArrayList<>();
        List<String> beyondDirList = new ArrayList<>();

        //检查是否越权
        checkTablePermission(fs , tableFsPath , paramFilePermission , paramDirPermission ,beyondFileList , beyondDirList);

        if(beyondFileList.size() > 0 || beyondDirList.size() > 0 ){
            //给分
            governanceAssessDetail.setAssessScore( BigDecimal.ZERO );
            //问题项
            governanceAssessDetail.setAssessProblem("目录或者文件访问权限超过建议值");
            //备注
            governanceAssessDetail.setAssessComment("越权的目录: " + beyondDirList + " , 越权的文件: " + beyondFileList);
        }

    }

    private void checkTablePermission(FileSystem fs, String tableFsPath, String paramFilePermission, String paramDirPermission, List<String> beyondFileList, List<String> beyondDirList) throws IOException {
        //判断当前目录是否越权
        FileStatus fileStatus = fs.getFileStatus(new Path(tableFsPath));
        //获取当前目录的权限
        FsPermission permission = fileStatus.getPermission();
        //判断是否越权
        boolean isBeyond = checkPermission( permission , paramDirPermission );

        if(isBeyond){
            beyondDirList.add( fileStatus.getPath().toString() );
        }

        //获取当前表目录下所有的内容
        FileStatus[] fileStatuses = fs.listStatus(new Path(tableFsPath));

        //递归判断是否越权
        checkDirOrFilePermission( fs ,fileStatuses , paramFilePermission, paramDirPermission , beyondFileList , beyondDirList);
    }

    /**
     * 递归判断目录和文件是否越权
     */
    private void checkDirOrFilePermission( FileSystem fs , FileStatus[] fileStatuses, String paramFilePermission, String paramDirPermission, List<String> beyondFileList, List<String> beyondDirList) throws IOException {
        for (FileStatus fileStatus : fileStatuses) {
            //文件
            if(fileStatus.isFile()){
                boolean isBeyond = checkPermission( fileStatus.getPermission() , paramFilePermission );
                if(isBeyond){
                    beyondFileList.add( fileStatus.getPath().toString()) ;
                }
            }else{
                //目录
                //判断当前目录
                boolean isBeyond = checkPermission( fileStatus.getPermission() , paramDirPermission );
                if(isBeyond){
                    beyondDirList.add( fileStatus.getPath().toString()) ;
                }

                //获取目录下的内容
                FileStatus[] subFileStatuses = fs.listStatus(fileStatus.getPath());
                checkDirOrFilePermission( fs ,subFileStatuses , paramFilePermission, paramDirPermission , beyondFileList , beyondDirList);
            }
        }
    }

    /**
     * 判断给定的目录 或者  文件 是否越权
     * @param permission
     * @param paramPermission
     * @return
     */
    private boolean checkPermission(FsPermission permission, String paramPermission) {
        //拆解标准权限
        // 644
        int userRWX = paramPermission.charAt(0) - '0';
        int groupRWX = paramPermission.charAt(1) - '0';
        int otherRWX = paramPermission.charAt(2) - '0';
        //取出 User Group Other 权限
        FsAction userAction = permission.getUserAction();
        FsAction groupAction = permission.getGroupAction();
        FsAction otherAction = permission.getOtherAction();

        //简单处理:  只要数字大， 就是越权
        if( userAction.ordinal()  > userRWX ) {
            return true ;
        }else if( groupAction.ordinal() > groupRWX){
            return true ;
        }else if ( otherAction.ordinal() > otherRWX){
            return true ;
        }else{
            return false ;
        }
    }

}
