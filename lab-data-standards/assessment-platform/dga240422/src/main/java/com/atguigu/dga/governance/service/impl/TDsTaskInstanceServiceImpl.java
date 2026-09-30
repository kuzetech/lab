package com.atguigu.dga.governance.service.impl;

import com.atguigu.dga.governance.bean.TDsTaskInstance;
import com.atguigu.dga.governance.mapper.TDsTaskInstanceMapper;
import com.atguigu.dga.governance.service.TDsTaskInstanceService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-02
 */
@Service
@DS("dolphinscheduler")
public class TDsTaskInstanceServiceImpl extends ServiceImpl<TDsTaskInstanceMapper, TDsTaskInstance> implements TDsTaskInstanceService {

    @Override
    public List<TDsTaskInstance> getBeforeNDaysTaskInstanceList(String name,  String startDate, String  endDate) {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .inSql( "id" , "SELECT  \n" +
                                "  MAX(id) max_id \n" +
                                "FROM t_ds_task_instance \n" +
                                "WHERE  NAME = '" + name + "'\n" +
                                "AND DATE_FORMAT(start_time , '%Y-%m-%d') >= '"+ startDate +"'\n" +
                                "AND DATE_FORMAT(start_time , '%Y-%m-%d') < '"+ endDate+"'\n" +
                                "AND state = 7 \n" +
                                "GROUP BY NAME , DATE_FORMAT(start_time , '%Y-%m-%d')")
        );
    }

    @Override
    public List<TDsTaskInstance> getFailTDsTaskInstanceList( String name, String assessDate) {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .eq("name" , name )
                        .eq("state" , 6)
                        .eq( "DATE_FORMAT(start_time , '%Y-%m-%d')" , assessDate)
        );
    }

    @Override
    public List<TDsTaskInstance> getTDsTaskInstanceListByJoin() {
        return getBaseMapper().selectTDsTaskInstanceList() ;
    }

    @Override
    public List<TDsTaskInstance> getTDsTaskInstanceListByIn() {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .inSql("id" , "SELECT \n" +
                                "   MAX(id) max_id \n" +
                                "FROM t_ds_task_instance\n" +
                                "WHERE state =  7 \n" +
                                "AND task_type = 'SHELL'\n" +
                                "GROUP BY NAME ")
        );
    }

    @Override
    public List<TDsTaskInstance> getTDsTaskInstanceListByExists() {
        return list(
                new QueryWrapper<TDsTaskInstance>()
                        .exists("SELECT 1 FROM \n" +
                                "( SELECT \n" +
                                "   MAX(id) max_id \n" +
                                "FROM t_ds_task_instance\n" +
                                "WHERE state =  7 \n" +
                                "AND task_type = 'SHELL'\n" +
                                "GROUP BY NAME  ) t2 \n" +
                                "WHERE t_ds_task_instance.id = t2.max_id")
        );
    }
}
