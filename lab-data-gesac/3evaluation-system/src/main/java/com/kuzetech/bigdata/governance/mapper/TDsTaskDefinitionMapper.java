package com.kuzetech.bigdata.governance.mapper;

import com.kuzetech.bigdata.governance.bean.TDsTaskDefinition;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author kuze
 * @since 2024-09-02
 */
@Mapper
@DS("dolphinscheduler")
public interface TDsTaskDefinitionMapper extends BaseMapper<TDsTaskDefinition> {

}
