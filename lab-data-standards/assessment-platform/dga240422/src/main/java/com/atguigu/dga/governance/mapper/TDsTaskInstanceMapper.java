package com.atguigu.dga.governance.mapper;

import com.atguigu.dga.governance.bean.TDsTaskInstance;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-02
 */
@Mapper
@DS("dolphinscheduler")
public interface TDsTaskInstanceMapper extends BaseMapper<TDsTaskInstance> {

    @Select(
            "SELECT \n" +
                    "t1.* \n" +
                    "FROM t_ds_task_instance  t1 \n" +
                    "JOIN \n" +
                    "(SELECT \n" +
                    "  NAME , MAX(id) max_id \n" +
                    "FROM t_ds_task_instance\n" +
                    "WHERE state =  7 \n" +
                    "AND task_type = 'SHELL'\n" +
                    "GROUP BY NAME ) t2 \n" +
                    "ON t1.id = t2.max_id"
    )
    List<TDsTaskInstance> selectTDsTaskInstanceList();
}
