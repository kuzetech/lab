package com.atguigu.dga.governance.mapper;

import com.atguigu.dga.governance.bean.GovernanceAssessTecOwner;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 技术负责人治理考评表 Mapper 接口
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-04
 */
@Mapper
@DS("dga")
public interface GovernanceAssessTecOwnerMapper extends BaseMapper<GovernanceAssessTecOwner> {

    @Select(
            " SELECT \n" +
                    "    assess_date , \n" +
                    "    tec_owner , \n" +
                    "    AVG(score_spec_avg) AS score_spec , \n" +
                    "    AVG(score_storage_avg) AS score_storage ,\n" +
                    "    AVG(score_spec_avg) AS score_calc ,\n" +
                    "    AVG(score_quality_avg) AS score_quality ,\n" +
                    "    AVG(score_security_avg) AS score_security ,\n" +
                    "    AVG(score_on_type_weight) AS score , \n" +
                    "    COUNT(*) AS table_num , \n" +
                    "    SUM(problem_num) AS problem_num , \n" +
                    "    NOW() AS create_time\n" +
                    "   \n" +
                    " FROM governance_assess_table \n" +
                    " WHERE assess_date = #{assessDate}\n" +
                    " GROUP BY assess_date , tec_owner"
    )
    List<GovernanceAssessTecOwner> selectGovernanceAssessTecOwnerList( String assessDate );
}
