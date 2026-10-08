package com.atguigu.dga.governance.service;

import com.atguigu.dga.governance.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 表治理考评情况 服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-04
 */
public interface GovernanceAssessTableService extends IService<GovernanceAssessTable> {

    void calcTableSorce( String assessDate );
}
