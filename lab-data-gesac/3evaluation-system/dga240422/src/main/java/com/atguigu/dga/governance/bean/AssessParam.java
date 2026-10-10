package com.atguigu.dga.governance.bean;

import com.atguigu.dga.meta.bean.TableMetaInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author WEIYUNHUI
 * @date 2024/8/31 9:18
 *
 * 用来封装考评参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssessParam {

    /**
     * 被考评表
     */
    private TableMetaInfo tableMetaInfo ;

    /**
     * 当前考评的指标
     */
    private GovernanceMetric governanceMetric ;

    /**
     * 考评日期
     */
    private String assessDate ;

    /**
     * 所有表
     */
    private List<TableMetaInfo> tableMetaInfoList;

    /**
     * DS中的任务定义
     */
    private TDsTaskDefinition tDsTaskDefinition ;

    /**
     * DS中的任务实例
     */
    private TDsTaskInstance tDsTaskInstance ;


}
