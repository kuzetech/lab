package com.atguigu.dga.governance.service;

import com.atguigu.dga.governance.bean.GovernanceAssessGlobal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理总考评表 服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-04
 */
public interface GovernanceAssessGlobalService extends IService<GovernanceAssessGlobal> {
    void calcGlobalScore(String assessDate);
}
