package com.kuzetech.bigdata.governance.service;

import com.kuzetech.bigdata.governance.bean.GovernanceAssessTable;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 表治理考评情况 服务类
 * </p>
 *
 * @author kuze
 * @since 2024-09-04
 */
public interface GovernanceAssessTableService extends IService<GovernanceAssessTable> {

    void calcTableSorce( String assessDate );
}
