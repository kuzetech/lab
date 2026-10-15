package com.kuzetech.bigdata.meta.service;

import com.kuzetech.bigdata.meta.bean.TableMetaInfo;
import com.kuzetech.bigdata.meta.bean.TableMetaInfoExtra;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 元数据表附加信息 服务类
 * </p>
 *
 *
 */
public interface TableMetaInfoExtraService extends IService<TableMetaInfoExtra> {

    /**
     * 初始化辅助信息
     */
    void initTableMetaInfoExtra(List<TableMetaInfo> tableMetaInfos);
}
