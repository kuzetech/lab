package com.atguigu.dga.meta.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoQuery;
import com.atguigu.dga.meta.bean.TableMetaInfoVO;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.atguigu.dga.util.SqlUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PipedReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-28
 */
@Service
@DS("dga")
public class TableMetaInfoServiceImpl extends ServiceImpl<TableMetaInfoMapper, TableMetaInfo> implements TableMetaInfoService {

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService ;

    @Autowired
    TableMetaInfoMapper tableMetaInfoMapper ;

    /**
     * 获取表信息列表的总记录数
     */
    @Override
    public Long getTableMetaInfoVOListCount(TableMetaInfoQuery tableMetaInfoQuery) {
        // 处理动态条件
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT\n" +
                        " count(*) cnt \n" +
                        " FROM table_meta_info  ti  JOIN table_meta_info_extra  te\n" +
                        " ON ti.schema_name = te.schema_name \n" +
                        " AND ti.table_name = te.table_name\n" +
                        " WHERE ti.assess_date = (SELECT MAX(assess_date) FROM table_meta_info ) \n"
        );
        //判断库名，等值判断
        if(tableMetaInfoQuery.getSchemaName() !=null && !tableMetaInfoQuery.getSchemaName().trim().isEmpty()){
            sqlBuilder.append(" AND ti.schema_name = '" + SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getSchemaName()) + "' \n" ) ;
        }
        //判断表名，支持模糊匹配
        if(tableMetaInfoQuery.getTableName() != null && !tableMetaInfoQuery.getTableName().trim().isEmpty()){
            sqlBuilder.append(" AND ti.table_name like '%" + SqlUtil.filterUnsafeSql( tableMetaInfoQuery.getTableName()) + "%' \n") ;
        }
        //判断数仓层级
        if(tableMetaInfoQuery.getDwLevel() != null && !tableMetaInfoQuery.getDwLevel().trim().isEmpty()){
            sqlBuilder.append(" AND te.dw_level = '" + SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getDwLevel()) + "' \n") ;
        }

        Long count = getBaseMapper().selectTableMetaInfoVOListCount(sqlBuilder.toString());

        return count;
    }

    /**
     * 根据前端传入的条件 以及 分页信息 查询表信息列表
     */
    @Override
    public List<TableMetaInfoVO> getTableMetaInfoVOList(TableMetaInfoQuery tableMetaInfoQuery) {
        // 处理动态条件
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT\n" +
                        "  ti.id,\n" +
                        "  ti.table_name,\n" +
                        "  ti.schema_name, \n" +
                        "  ti.table_size,\n" +
                        "  ti.table_total_size, \n" +
                        "  ti.table_comment, \n" +
                        "  ti.table_last_modify_time , \n" +
                        "  ti.table_last_access_time , \n" +
                        "  te.tec_owner_user_name, \n" +
                        "  te.busi_owner_user_name  \n" +
                        " FROM table_meta_info  ti  JOIN table_meta_info_extra  te\n" +
                        " ON ti.schema_name = te.schema_name \n" +
                        " AND ti.table_name = te.table_name\n" +
                        " WHERE ti.assess_date = (SELECT MAX(assess_date) FROM table_meta_info ) \n"
        );
        //判断库名，等值判断
        if(tableMetaInfoQuery.getSchemaName() !=null && !tableMetaInfoQuery.getSchemaName().trim().isEmpty()){
            sqlBuilder.append(" AND ti.schema_name = '" + SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getSchemaName()) + "' \n" ) ;
        }
        //判断表名，支持模糊匹配
        if(tableMetaInfoQuery.getTableName() != null && !tableMetaInfoQuery.getTableName().trim().isEmpty()){
            sqlBuilder.append(" AND ti.table_name like '%" + SqlUtil.filterUnsafeSql( tableMetaInfoQuery.getTableName()) + "%' \n") ;
        }
        //判断数仓层级
        if(tableMetaInfoQuery.getDwLevel() != null && !tableMetaInfoQuery.getDwLevel().trim().isEmpty()){
            sqlBuilder.append(" AND te.dw_level = '" + SqlUtil.filterUnsafeSql(tableMetaInfoQuery.getDwLevel()) + "' \n") ;
        }

        //处理分页
        //计算开始行:  (  pageNo - 1  ) * pageSize
        Integer pageNo = tableMetaInfoQuery.getPageNo() ;
        Integer pageSize = tableMetaInfoQuery.getPageSize() ;
        Integer start = ( pageNo - 1) * pageSize ;
        sqlBuilder.append( " LIMIT " + start + " , " + pageSize) ;

        //baseMapper.selectTableMetaInfoVOList()
        //tableMetaInfoMapper.selectTableMetaInfoVOList()
        List<TableMetaInfoVO> tableMetaInfoVOList = getBaseMapper().selectTableMetaInfoVOList(sqlBuilder.toString());

        return tableMetaInfoVOList;
    }

    /**
     * 提取Hive和hdfs的元数据信息
     * @param schemaName  指定要提取哪个库下的元数据信息
     * @param assessDate  指定考评的日期，
     *                    1.开发时方便指定日期进行考评 ， 考虑到数据仓中的数据没有当日的数据
     *
     *                    2.支持项目上线后指定日期进行考评
     *
     * 步骤:
     *    0. 清除当日提取的元数据信息(幂等处理)
     *
     *    1. 提取Hive的元数据信息
     *
     *    2. 提取Hdfs的元数据信息
     *
     *    3. 将提取的元数据信息写入到数据库表中
     *
     *    4. 初始化表的辅助信息
     *
     */
    @Override
    public void initTableMetaInfo(String schemaName, String assessDate) {

        try {
            // 0. 清除当日提取的元数据信息
            this.remove(
                    new QueryWrapper<TableMetaInfo>()
                            .eq("assess_date" , assessDate)
            );

            // 1. 提取Hive的元数据信息
            // 获取hive中指定库下所有的表
            List<String> allTableNames = hiveClient.getAllTables(schemaName);
            //System.out.println("allTableNames = " + allTableNames);

            //创建集合， 维护处理好的TableMetaInfo对象
            List<TableMetaInfo> tableMetaInfos = new ArrayList<>(allTableNames.size());

            //获取所有的表对象，并处理每个表对象
            for (String tableName : allTableNames) {
                //获取表对象
                Table table = hiveClient.getTable(schemaName, tableName);
                //System.out.println(table);

                //从Table中提取元数据信息， 封装到TableMetaInfo对象中
                TableMetaInfo tableMetaInfo = extractTableMetaInfoFromHive(table);

                // 2. 提取Hdfs的元数据信息
                extractTableMetaInfoFromHdfs(tableMetaInfo);
                //System.out.println("tableMetaInfo = " + tableMetaInfo);

                //补充其他的信息
                tableMetaInfo.setAssessDate( assessDate );
                tableMetaInfo.setCreateTime( new Date() );

                // 3. 将提取的元数据信息写入到数据库表中
                // 在循环中与外部的组件（例如数据库）进行交互不推荐的。
                // this.save( tableMetaInfo ) ;

                // 攒批
                tableMetaInfos.add(tableMetaInfo) ;
            }
            // 3. 将提取的元数据信息写入到数据库表中
            // 批写到数据库表中
            this.saveBatch( tableMetaInfos );

            // 4. 初始化表的辅助信息
            tableMetaInfoExtraService.initTableMetaInfoExtra(tableMetaInfos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *  从HDFS中提取元数据信息， 封装到TableMetaInfo对象中
     * @param tableMetaInfo
     */
    private void extractTableMetaInfoFromHdfs(TableMetaInfo tableMetaInfo) throws Exception {
        //创建文件系统对象
        FileSystem fs = FileSystem.get(
                new URI(tableMetaInfo.getTableFsPath()),
                new Configuration(),
                tableMetaInfo.getTableFsOwner()
        );

        //获取当前对在hdfs中的所有内容( 目录 + 文件 )
        FileStatus[] fileStatuses = fs.listStatus(new Path(tableMetaInfo.getTableFsPath()));

        //通过递归的方式汇总 表大小 、 表总大小 、 表最后修改时间 、 访问时间
        addHdfsInfo(fs , fileStatuses , tableMetaInfo );

        //hdfs文件系统的容量信息
        tableMetaInfo.setFsCapcitySize( fs.getStatus().getCapacity());
        tableMetaInfo.setFsUsedSize( fs.getStatus().getUsed());
        tableMetaInfo.setFsRemainSize( fs.getStatus().getRemaining());
    }

    /**
     * 递归汇总 表大小 、 表总大小 、 表最后修改时间 、 访问时间
     * @param fs
     * @param fileStatuses
     * @param tableMetaInfo
     *
     * 递归的思想: 自己调用自己
     *    注意:  递归一定要有终止条件
     *
     *    过程:
     *        1.处理 ， 如果是文件， 提取文件的相关信息
     *
     *        2.下探 ， 如果是目录， 继续进入到该目录中获取对应的内容，继续处理 + 下探
     */
    private void addHdfsInfo(FileSystem fs, FileStatus[] fileStatuses, TableMetaInfo tableMetaInfo) throws IOException {
        for (FileStatus fileStatus : fileStatuses) {
            //判断是否文件还是目录
            if(fileStatus.isFile()){
                //处理文件
                //表大小
                tableMetaInfo.setTableSize( (tableMetaInfo.getTableSize() == null ? 0L : tableMetaInfo.getTableSize()) +  fileStatus.getLen() );
                //表总大小
                tableMetaInfo.setTableTotalSize( ( tableMetaInfo.getTableTotalSize() == null ? 0L : tableMetaInfo.getTableTotalSize() ) + fileStatus.getLen() * fileStatus.getReplication());
                //最后访问时间
                long fileLastAccessTime = fileStatus.getAccessTime();
                long currLastAccessTime = tableMetaInfo.getTableLastAccessTime()== null ?  0L :  tableMetaInfo.getTableLastAccessTime().getTime();
                long lastAccessTime = Math.max(fileLastAccessTime, currLastAccessTime);
                tableMetaInfo.setTableLastAccessTime( new Date( lastAccessTime ));

                //最后修改时间
                long fileLastModifyTime = fileStatus.getModificationTime();
                long currLastModifyTime = tableMetaInfo.getTableLastModifyTime() == null ? 0L : tableMetaInfo.getTableLastModifyTime() .getTime();
                long lastModifyTime = Math.max(fileLastModifyTime, currLastModifyTime);
                tableMetaInfo.setTableLastModifyTime( new Date( lastModifyTime ));
            }else{
                //继续下探
                //获取当前目录下的内容
                FileStatus[] subFileStatuses = fs.listStatus(fileStatus.getPath());
                addHdfsInfo( fs , subFileStatuses , tableMetaInfo );
            }
        }
    }

    /**
     * 从Table中提取元数据信息， 封装到TableMetaInfo对象中
     * @param table
     * @return
     */
    private TableMetaInfo extractTableMetaInfoFromHive(Table table) {
        TableMetaInfo tableMetaInfo = new TableMetaInfo() ;
        // get Table  -> set  TableMetaInfo
        //库名
        tableMetaInfo.setSchemaName(table.getDbName());
        //表名
        tableMetaInfo.setTableName(table.getTableName());
        //列信息
        //转json的过程中，过滤掉不需要的字段，只保留需要的。
        PropertyPreFilters propertyPreFilters = new PropertyPreFilters();
        PropertyPreFilters.MySimplePropertyPreFilter filter = propertyPreFilters.addFilter("comment", "name", "type");
        tableMetaInfo.setColNameJson(JSON.toJSONString(table.getSd().getCols() , filter));
        //分区列信息
        tableMetaInfo.setPartitionColNameJson(JSON.toJSONString(table.getPartitionKeys() , filter));
        //hdfs所属人
        tableMetaInfo.setTableFsOwner( table.getOwner() );
        //表参数
        tableMetaInfo.setTableParametersJson(JSON.toJSONString(table.getParameters()));
        //表描述
        tableMetaInfo.setTableComment(table.getParameters().get("comment"));
        //表路径
        tableMetaInfo.setTableFsPath(table.getSd().getLocation());
        //表输入格式
        tableMetaInfo.setTableInputFormat(table.getSd().getInputFormat());
        //表输出格式
        tableMetaInfo.setTableOutputFormat(table.getSd().getOutputFormat());
        //行格式
        tableMetaInfo.setTableRowFormatSerde(table.getSd().getSerdeInfo().getSerializationLib());
        //表创建时间
        tableMetaInfo.setTableCreateTime(new Date( table.getCreateTime() * 1000L ));
        //表类型
        tableMetaInfo.setTableType(table.getTableType());
        //表的分桶列
        tableMetaInfo.setTableBucketColsJson(JSON.toJSONString(table.getSd().getBucketCols()));
        //表的分桶数
        tableMetaInfo.setTableBucketNum((long)table.getSd().getNumBuckets());
        //表的分桶排序字段
        tableMetaInfo.setTableSortColsJson(JSON.toJSONString(table.getSd().getSortCols()));

        return tableMetaInfo;
    }

    // 定义Hive的元数据客户端对象
    IMetaStoreClient hiveClient;

    @Value("${hive.metastore.uris}")
    String hiveMetaStoreUris ;

    /**
     * 创建Hive的元数据客户端
     *
     * 0.hive元数据服务的地址
     *    <property>
     *      <name>hive.metastore.uris</name>
     *      <value>thrift://hadoop102:9083</value>
     *   </property>
     *
     * 1.先启动hive的元数据服务
     *
     * 2.确保服务启动成功 netstat -nltp | grep 9083
     */
    @PostConstruct  // 在容器管理好当前类的对象后，自动调用该方法 ， 时机在我们调用initTableMetaInfo之前
    private void createHiveMetaStoreClient(){
        //Configuration configuration = new Configuration();
        //configuration.set("hive.metastore.uris", "thrift://hadoop102:9083");

        HiveConf hiveConf = new HiveConf();
        MetastoreConf.setVar(hiveConf , MetastoreConf.ConfVars.THRIFT_URIS , hiveMetaStoreUris);

        try {
            hiveClient = new HiveMetaStoreClient(hiveConf);
            System.out.println("hiveClient :" + hiveClient);
        } catch (MetaException e) {
            throw new RuntimeException("获取Hive元数据客户端失败....");
        }
    }
}
