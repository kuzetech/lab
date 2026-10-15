package com.kuzetech.bigdata.governance.service.impl;

import com.kuzetech.bigdata.governance.bean.GovernanceMetric;
import com.kuzetech.bigdata.governance.mapper.GovernanceMetricMapper;
import com.kuzetech.bigdata.governance.service.GovernanceMetricService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 考评指标参数表 服务实现类
 * </p>
 *
 * @author kuze
 * @since 2024-08-30
 */
@Service
@DS("dga")
public class GovernanceMetricServiceImpl extends ServiceImpl<GovernanceMetricMapper, GovernanceMetric> implements GovernanceMetricService {

}
