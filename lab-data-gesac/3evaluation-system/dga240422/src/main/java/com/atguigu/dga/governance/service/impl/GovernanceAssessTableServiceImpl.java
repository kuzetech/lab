package com.atguigu.dga.governance.service.impl;

import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.bean.GovernanceAssessTable;
import com.atguigu.dga.governance.bean.GovernanceType;
import com.atguigu.dga.governance.mapper.GovernanceAssessTableMapper;
import com.atguigu.dga.governance.service.GovernanceAssessTableService;
import com.atguigu.dga.governance.service.GovernanceTypeService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 表治理考评情况 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-04
 */
@Service
@DS("dga")
public class GovernanceAssessTableServiceImpl extends ServiceImpl<GovernanceAssessTableMapper, GovernanceAssessTable> implements GovernanceAssessTableService {

    @Autowired
    GovernanceTypeService governanceTypeService ;
    /**
     * 汇总表级分数
     * @param assessDate
     */
    @Override
    public void calcTableSorce(String assessDate) {
        //清空当前考评日期对应的结果
        remove(
                new QueryWrapper<GovernanceAssessTable>()
                        .eq("assess_date" , assessDate)
        );

        List<GovernanceAssessTable> governanceAssessTables = getBaseMapper().selectGovernanceAssessTableList(assessDate);

        //查询指标权重
        Map<String, BigDecimal> typeWeightMap = new HashMap<>();
        List<GovernanceType> governanceTypeList = governanceTypeService.list();
        governanceTypeList.forEach(
                governanceType ->  typeWeightMap.put(governanceType.getTypeCode() , governanceType.getTypeWeight() )
        );

        //计算五维权重后总分
        for (GovernanceAssessTable governanceAssessTable : governanceAssessTables) {

            BigDecimal specScore =
                    governanceAssessTable.getScoreSpecAvg()
                            .multiply(typeWeightMap.get(DgaConstant.GOVERNANCE_TYPE_SPEC))
                            .divide(BigDecimal.TEN);
            BigDecimal storageScore =
                    governanceAssessTable.getScoreStorageAvg()
                            .multiply(typeWeightMap.get(DgaConstant.GOVERNANCE_TYPE_STORAGE))
                            .divide(BigDecimal.TEN);
            BigDecimal calcScore =
                    governanceAssessTable.getScoreCalcAvg()
                            .multiply(typeWeightMap.get(DgaConstant.GOVERNANCE_TYPE_CALC))
                            .divide(BigDecimal.TEN);
            BigDecimal qualityScore =
                    governanceAssessTable.getScoreQualityAvg()
                            .multiply(typeWeightMap.get(DgaConstant.GOVERNANCE_TYPE_QUALITY))
                            .divide(BigDecimal.TEN);
            BigDecimal securityScore =
                    governanceAssessTable.getScoreSecurityAvg()
                            .multiply(typeWeightMap.get(DgaConstant.GOVERNANCE_TYPE_SECURITY))
                            .divide(BigDecimal.TEN);

            BigDecimal scoreOnTypeWeight = specScore.add(storageScore).add(calcScore).add(qualityScore).add(securityScore);

            governanceAssessTable.setScoreOnTypeWeight( scoreOnTypeWeight );

        }

        //写入到数据库表中
        saveBatch( governanceAssessTables ) ;

    }
}
