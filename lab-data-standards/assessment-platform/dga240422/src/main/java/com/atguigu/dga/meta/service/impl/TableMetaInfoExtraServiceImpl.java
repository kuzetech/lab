package com.atguigu.dga.meta.service.impl;

import static com.atguigu.dga.constant.DgaConstant.*;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.mapper.TableMetaInfoExtraMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 元数据表附加信息 服务实现类
 * </p>
 *
 * @author zhaocunquan
 * @since 2024-08-28
 */
@Service
@DS("dga")
public class TableMetaInfoExtraServiceImpl extends ServiceImpl<TableMetaInfoExtraMapper, TableMetaInfoExtra> implements TableMetaInfoExtraService {

    /**
     * 初始化辅助信息
     *
     * 每张表只需要被初始化一次即可。 但是考虑到数仓中会增加新的表， 所有每天提取元数据的过程中，都会调用一次该方法，
     * 同时将当前提取到的所有的数仓表都传递到该方法中， 因此在该方法中处理每张表的时候需要进行判断， 如果当前表已经被
     * 初始化过，直接跳过， 只针对没有被初始化的表进行操作。
     * @param tableMetaInfos
     */
    @Override
    /*
    public void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfos) {

        List<TableMetaInfoExtra> tableMetaInfoExtras = new ArrayList<>(tableMetaInfos.size());

        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {
            //到table_meta_info_extra表中查询是否有当前表的信息
            TableMetaInfoExtra tableMetaInfoExtra = this.getOne(
                    new QueryWrapper<TableMetaInfoExtra>()
                            .eq("schema_name", tableMetaInfo.getSchemaName())
                            .eq("table_name", tableMetaInfo.getTableName())
            );

            if(tableMetaInfoExtra == null){
                //当前表没有被初始化过
                tableMetaInfoExtra = new TableMetaInfoExtra() ;
                //库名
                tableMetaInfoExtra.setSchemaName(tableMetaInfo.getSchemaName());
                //表名
                tableMetaInfoExtra.setTableName(tableMetaInfo.getTableName());
                //技术Owner
                tableMetaInfoExtra.setTecOwnerUserName(TEC_OWNER_UNSET);
                //业务Owner
                tableMetaInfoExtra.setBusiOwnerUserName(BUSI_OWNER_UNSET);
                //生命周期类型
                tableMetaInfoExtra.setLifecycleType(LIFECYCLE_TYPE_UNSET);
                //生命周期天数
                tableMetaInfoExtra.setLifecycleDays(LIFECYCLE_DAYS_UNSET);
                //安全级别
                tableMetaInfoExtra.setSecurityLevel(SECURITY_LEVEL_UNSET);
                //数仓层级
                tableMetaInfoExtra.setDwLevel( getDwLevel(tableMetaInfo.getTableName().toUpperCase()) );
                //创建时间
                tableMetaInfoExtra.setCreateTime( new Date());

                //攒批
                tableMetaInfoExtras.add( tableMetaInfoExtra ) ;
            }
        }

        //批写到数据库表中
        this.saveBatch(tableMetaInfoExtras) ;
    }

     */

    public void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfos) {

        //一次性从table_meta_info_extra表中查询出所有的数据
        List<TableMetaInfoExtra> tableMetaInfoExtras = this.list();

        List<String> tableMetaInfoNameList =
                tableMetaInfos.stream().map(tableMetaInfo -> tableMetaInfo.getSchemaName() + "." + tableMetaInfo.getTableName()).collect(Collectors.toList());

        List<String> tableMetaInfoExtraNameList =
                tableMetaInfoExtras.stream().map(tableMetaInfoExtra -> tableMetaInfoExtra.getSchemaName() + "." + tableMetaInfoExtra.getTableName()).collect(Collectors.toList());
        //两个集合进行差值计算
        Collection subtractNameList = CollectionUtils.subtract(tableMetaInfoNameList, tableMetaInfoExtraNameList);

        Map<String, TableMetaInfo> tableMetaInfoHashMap= new HashMap<>();
        for (TableMetaInfo tableMetaInfo : tableMetaInfos) {
            tableMetaInfoHashMap.put( tableMetaInfo.getSchemaName()+"." + tableMetaInfo.getTableName() , tableMetaInfo);
        }

        ArrayList<TableMetaInfo> subtractTableMetaInfoList = new ArrayList<>();
        for (Object o : subtractNameList) {
            TableMetaInfo tableMetaInfo = tableMetaInfoHashMap.get(o.toString());
            subtractTableMetaInfoList.add( tableMetaInfo ) ;
        }


        List<TableMetaInfoExtra> tableMetaInfoExtraResult = new ArrayList<>(tableMetaInfos.size());

        for (TableMetaInfo tableMetaInfo : subtractTableMetaInfoList) {
            TableMetaInfoExtra tableMetaInfoExtra = new TableMetaInfoExtra();
                //当前表没有被初始化过
                tableMetaInfoExtra = new TableMetaInfoExtra() ;
                //库名
                tableMetaInfoExtra.setSchemaName(tableMetaInfo.getSchemaName());
                //表名
                tableMetaInfoExtra.setTableName(tableMetaInfo.getTableName());
                //技术Owner
                tableMetaInfoExtra.setTecOwnerUserName(TEC_OWNER_UNSET);
                //业务Owner
                tableMetaInfoExtra.setBusiOwnerUserName(BUSI_OWNER_UNSET);
                //生命周期类型
                tableMetaInfoExtra.setLifecycleType(LIFECYCLE_TYPE_UNSET);
                //生命周期天数
                tableMetaInfoExtra.setLifecycleDays(LIFECYCLE_DAYS_UNSET);
                //安全级别
                tableMetaInfoExtra.setSecurityLevel(SECURITY_LEVEL_UNSET);
                //数仓层级
                tableMetaInfoExtra.setDwLevel( getDwLevel(tableMetaInfo.getTableName().toUpperCase()) );
                //创建时间
                tableMetaInfoExtra.setCreateTime( new Date());

                //攒批
               tableMetaInfoExtraResult.add( tableMetaInfoExtra ) ;
            }
         //批写到数据库表中
        this.saveBatch(tableMetaInfoExtraResult) ;
    }

    /**
     * 通过表名，判定所属的数仓层级
     */
    private String getDwLevel(String tableName){

        if(tableName.startsWith(DW_LEVEL_ODS)){
            return DW_LEVEL_ODS;
        } else if (tableName.startsWith(DW_LEVEL_DWD)) {
            return DW_LEVEL_DWD;
        }else if (tableName.startsWith(DW_LEVEL_DIM)) {
            return DW_LEVEL_DIM;
        }else if (tableName.startsWith(DW_LEVEL_DWS)) {
            return DW_LEVEL_DWS;
        }else if (tableName.startsWith(DW_LEVEL_ADS)) {
            return DW_LEVEL_ADS;
        }else if (tableName.startsWith(DW_LEVEL_DM)) {
            return DW_LEVEL_DM;
        }else  {
            return DW_LEVEL_OTHER;
        }

    }
}
