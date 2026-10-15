package com.kuzetech.bigdata.governance.service.impl;

import com.kuzetech.bigdata.governance.bean.GovernanceType;
import com.kuzetech.bigdata.governance.mapper.GovernanceTypeMapper;
import com.kuzetech.bigdata.governance.service.GovernanceTypeService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 治理考评类别权重表 服务实现类
 * </p>
 *
 * @author kuze
 * @since 2024-08-30
 */
@Service
@DS("dga")
public class GovernanceTypeServiceImpl extends ServiceImpl<GovernanceTypeMapper, GovernanceType> implements GovernanceTypeService {

}
