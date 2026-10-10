package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoQuery;
import com.atguigu.dga.meta.bean.TableMetaInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 元数据表 服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-28
 */
public interface TableMetaInfoService extends IService<TableMetaInfo> {

    /**
     * 根据条件处理表信息列表的总数
     */
    Long getTableMetaInfoVOListCount(TableMetaInfoQuery tableMetaInfoQuery );

    /**
     * 根据条件处理表信息列表的查询
     */
    List<TableMetaInfoVO> getTableMetaInfoVOList(TableMetaInfoQuery tableMetaInfoQuery );




    /**
     * 提取Hive和hdfs的元数据信息
     * @param schemaName  指定要提取哪个库下的元数据信息
     * @param assessDate  指定考评的日期，
     *                    1.开发时方便指定日期进行考评 ， 考虑到数据仓中的数据没有当日的数据
     *
     *                    2.支持项目上线后指定日期进行考评
     */
    void initTableMetaInfo(String schemaName ,  String assessDate );
}
