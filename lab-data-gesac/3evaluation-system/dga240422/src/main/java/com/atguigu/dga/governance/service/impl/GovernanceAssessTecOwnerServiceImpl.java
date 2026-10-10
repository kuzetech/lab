package com.atguigu.dga.governance.service.impl;

import com.atguigu.dga.governance.bean.GovernanceAssessTecOwner;
import com.atguigu.dga.governance.mapper.GovernanceAssessTecOwnerMapper;
import com.atguigu.dga.governance.service.GovernanceAssessTecOwnerService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 技术负责人治理考评表 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-04
 */
@Service
@DS("dga")
public class GovernanceAssessTecOwnerServiceImpl extends ServiceImpl<GovernanceAssessTecOwnerMapper, GovernanceAssessTecOwner> implements GovernanceAssessTecOwnerService {

    /**
     * 核算人级分数
     * @param assessDate
     */
    @Override
    public void calcTecOwnerScore(String assessDate) {
        //清空考评当日的结果
        remove(
                new QueryWrapper<GovernanceAssessTecOwner>()
                        .eq("assess_date" , assessDate)
        ) ;

        List<GovernanceAssessTecOwner> governanceAssessTecOwners = getBaseMapper().selectGovernanceAssessTecOwnerList(assessDate);

        //写入到数据库表中
        saveBatch(governanceAssessTecOwners) ;
    }
}
