package com.kuzetech.bigdata.governance.service;

import com.kuzetech.bigdata.governance.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理考评结果明细 服务类
 * </p>
 *
 * @author kuze
 * @since 2024-08-30
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {

    /**
     * 主考评方法
     */
    void mainAssess( String assessDate );
}
