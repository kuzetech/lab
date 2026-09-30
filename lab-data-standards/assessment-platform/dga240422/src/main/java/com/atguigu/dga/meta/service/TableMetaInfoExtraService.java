package com.atguigu.dga.meta.service;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 元数据表附加信息 服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-28
 */
public interface TableMetaInfoExtraService extends IService<TableMetaInfoExtra> {

    /**
     * 初始化辅助信息
     */
    void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfos);
}
