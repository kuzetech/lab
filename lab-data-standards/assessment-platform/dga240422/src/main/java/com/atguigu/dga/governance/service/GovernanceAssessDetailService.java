package com.atguigu.dga.governance.service;

import com.atguigu.dga.governance.bean.GovernanceAssessDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 治理考评结果明细 服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-30
 */
public interface GovernanceAssessDetailService extends IService<GovernanceAssessDetail> {

    /**
     * 主考评方法
     */
    void mainAssess( String assessDate );
}
