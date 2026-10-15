package com.kuzetech.bigdata.governance.service.impl;

import com.kuzetech.bigdata.governance.bean.GovernanceAssessGlobal;
import com.kuzetech.bigdata.governance.mapper.GovernanceAssessGlobalMapper;
import com.kuzetech.bigdata.governance.service.GovernanceAssessGlobalService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 治理总考评表 服务实现类
 * </p>
 *
 * @author kuze
 * @since 2024-09-04
 */
@Service
@DS("dga")
public class GovernanceAssessGlobalServiceImpl extends ServiceImpl<GovernanceAssessGlobalMapper, GovernanceAssessGlobal> implements GovernanceAssessGlobalService {

    /**
     * 核算全局分数
     * @param assessDate
     */
    @Override
    public void calcGlobalScore(String assessDate) {
        //删除考评日期当天的结果
        remove(
                new QueryWrapper<GovernanceAssessGlobal>()
                        .eq("assess_date" , assessDate)
        );

        GovernanceAssessGlobal governanceAssessGlobal = getBaseMapper().selectGovernanceAssessGlobal(assessDate);

        //写入到数据库表中
        save( governanceAssessGlobal );
    }
}
