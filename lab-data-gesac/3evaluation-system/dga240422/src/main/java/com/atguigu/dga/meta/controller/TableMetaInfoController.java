package com.atguigu.dga.meta.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.atguigu.dga.meta.bean.TableMetaInfoQuery;
import com.atguigu.dga.meta.bean.TableMetaInfoVO;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 元数据表 前端控制器
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-28
 */
@RestController
@RequestMapping("/tableMetaInfo")
public class TableMetaInfoController {

    private static final String SUCCESS = "success" ;

    @Autowired
    TableMetaInfoService tableMetaInfoService ;

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService ;


    /**
     * 手动更新全库元数据
     *
     * 接口请求地址: /tableMetaInfo/init-tables/{database}/{assessdate}
     *
     * 接口请求方式: POST
     *
     * 接口参数: 接口路径中的{database}和{assessdate}
     *
     * 接口返回值: "success"
     */

    @PostMapping("/init-tables/{database}/{assessdate}")
    public String initTables( @PathVariable("database") String database,  @PathVariable("assessdate") String assessDate){

        tableMetaInfoService.initTableMetaInfo( database , assessDate);

        return SUCCESS ;
    }


    /**
     * 辅助信息修改接口
     *
     * 接口路径: /tableMetaInfo/tableExtra
     *
     * 接口请求方式: POST
     *
     * 接口的参数:
     *    {
     *   "id": 1,
     *   "tableName": "ads_traffic_stats_by_channel",
     *   "schemaName": "gmall",
     *   "tecOwnerUserName": "weiyunhui",
     *   "busiOwnerUserName": "weiyunhui",
     *   "lifecycleDays": 12,
     *   "securityLevel": "1",
     *   "dwLevel": "ADS",
     *   "createTime": "2023-04-15T07:35:09.000+00:00",
     *   "updateTime": null
     *  }
     *
     * 接口返回值: "success"
     */

    @PostMapping("/tableExtra")
    public String tableExtra( @RequestBody TableMetaInfoExtra tableMetaInfoExtra){
        // 设置修改时间
        tableMetaInfoExtra.setUpdateTime( new Date());
        // 修改辅助信息
        tableMetaInfoExtraService.saveOrUpdate(tableMetaInfoExtra) ;

        return SUCCESS ;
    }


    /**
     * 单表详情查询接口
     *
     * 接口路径: /tableMetaInfo/table/{tableMetaInfoId}
     *
     * 接口请求方式: GET
     *
     * 接口参数: 请求路径中的tableMetaInfoId
     *
     * 接口返回值结构:
     *   {"id":1186,"tableName":"ads_traffic_stats_by_channel","schemaName":"gmall","colNameJson":"[{\"comment\":\"统计日期\",\"name\":\"dt\",\"type\":\"string\"},{\"comment\":\"最近天数,1:最近1天,7:最近7天,30:最近30天\",\"name\":\"recent_days\",\"type\":\"bigint\"},{\"comment\":\"渠道\",\"name\":\"channel\",\"type\":\"str  ing\"},{\"comment\":\"访客人数\",\"name\":\"uv_count\",\"type\":\"bigint\"},{\"comment\":\"会话平均停留时长，单位为秒\",\"name\":\"avg_duration_sec\",\"type\":\"bigint\"},{\"comment\":\"会话平均浏览页面数\",\"name\":\"avg_page_count\",\"type\":\"bigint\"},{\"comment\":\"会话数\",\"name\":\"sv_count\",\"type\":\"bigint\"},{\"comment\":\"跳出率\",\"name\":\"bounce_rate\",\"type\":\"decimal(16,2)\"}]","partitionColNameJson":"[]","tableFsOwner":"atguigu","tableParametersJson":"{\"totalSize\":\"2254\",\"EXTERNAL\":\"TRUE\",\"numFiles\":\"2\",\"transient_lastDdlTime\":\"1680235106\",\"bucketing_version\":\"2\",\"comment\":\"各渠道流量统计\"}","tableComment":"各渠道流量统计","tableFsPath":"hdfs://hadoop102:8020/warehouse/gmall/ads/ads_traffic_stats_by_channel","tableInputFormat":"org.apache.hadoop.mapred.TextInputFormat","tableOutputFormat":"org.apache.hadoop.mapred.TextInputFormat","tableRowFormatSerde":"org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe","tableCreateTime":"2023-03-31T03:58:26.000+00:00","tableType":"EXTERNAL_TABLE","tableBucketColsJson":null,"tableBucketNum":-1,"tableSortColsJson":null,"tableSize":2254,"tableTotalSize":6762,"tableLastModifyTime":"2023-02-07T09:52:58.000+00:00","tableLastAccessTime":"2023-02-07T09:52:58.000+00:00","fsCapcitySize":141652144128,"fsUsedSize":10058940416,"fsRemainSize":84828000256,"assessDate":"2023-04-15","createTime":"2023-04-15T07:35:05.000+00:00","updateTime":null,"tableMetaInfoExtra":{"id":1,"tableName":"ads_traffic_stats_by_channel","schemaName":"gmall","tecOwnerUserName":"weiyunhui","busiOwnerUserName":"weiyunhui","lifecycleDays":12,"securityLevel":"1","dwLevel":"ADS","createTime":"2023-04-15T07:35:09.000+00:00","updateTime":null}}
     *
     */

    @GetMapping("/table/{tableMetaInfoId}")
    public String table( @PathVariable("tableMetaInfoId") Long tableMetaInfoId){
        // 查询tableMetaInfo对象
        TableMetaInfo tableMetaInfo = tableMetaInfoService.getOne(
                new QueryWrapper<TableMetaInfo>()
                        .eq("id", tableMetaInfoId)
                        .inSql("assess_date", "SELECT MAX(assess_date) FROM table_meta_info")
                        //.last(" AND assess_date = (SELECT MAX(assess_date) FROM table_meta_info) ")
        );

        // 查询TableMetaInfoExtra对象
        TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraService.getOne(
                new QueryWrapper<TableMetaInfoExtra>()
                        .eq("table_name", tableMetaInfo.getTableName())
                        .eq("schema_name", tableMetaInfo.getSchemaName())
        );

        tableMetaInfo.setTableMetaInfoExtra( tableMetaInfoExtra );

        return JSON.toJSONString( tableMetaInfo ) ;
    }


    /**
     * 表信息列表接口
     *
     * 接口路径: /tableMetaInfo/table-list
     *
     * 接口请求方式: GET
     *
     * 接口请求参数:  pageNo,
     *              pageSize,
     *              tableName,
     *              schemaName,
     *              dwLevel
     *
     * 接口返回值结构:
     *          {
     *            "total": 79,
     *            "list": [
     *              { "id": 1186,
     *                "tableName": "ads_traffic_stats_by_channel",
     *                "schemaName": "gmall",
     *                "tableSize": 2254,
     *                "tableTotalSize": 6762,
     *                "tableComment": "各渠道流量统计",
     *                "tecOwnerUserName": "weiyunhui",
     *                "busiOwnerUserName": "weiyunhui",
     *                "tableLastModifyTime": "2023-02-07T09:52:58.000+00:00",
     *                "tableLastAccessTime": "2023-02-07T09:52:58.000+00:00"
     *              },
     *              { "id": 1187,
     *                ….
     *              }
     *            ]
     *          }
     */

    @GetMapping("/table-list")
    public String tableList(  TableMetaInfoQuery tableMetaInfoQuery ){
        System.out.println("tableMetaInfoQuery = " + tableMetaInfoQuery);
        //调用service
        //查询列表
        List<TableMetaInfoVO> tableMetaInfoVOList =
                tableMetaInfoService.getTableMetaInfoVOList(tableMetaInfoQuery);

        //查询总数
        Long count = tableMetaInfoService.getTableMetaInfoVOListCount(tableMetaInfoQuery);

        JSONObject resultJsonObj = new JSONObject();
        resultJsonObj.put("total" , count);
        resultJsonObj.put("list" ,tableMetaInfoVOList );

        return resultJsonObj.toJSONString() ;
    }
}
