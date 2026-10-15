package com.kuzetech.bigdata.governance.service;

import com.kuzetech.bigdata.governance.bean.GovernanceAssessGlobal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理总考评表 服务类
 * </p>
 *
 * @author kuze
 * @since 2024-09-04
 */
public interface GovernanceAssessGlobalService extends IService<GovernanceAssessGlobal> {
    void calcGlobalScore(String assessDate);
}
