package com.atguigu.dga.governance.service;

import com.atguigu.dga.governance.bean.TDsTaskInstance;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-02
 */
public interface TDsTaskInstanceService extends IService<TDsTaskInstance> {

    List<TDsTaskInstance> getBeforeNDaysTaskInstanceList(String name, String  startDate, String  endDate);
    List<TDsTaskInstance> getFailTDsTaskInstanceList(String name , String assessDate);
    List<TDsTaskInstance> getTDsTaskInstanceListByJoin();

    List<TDsTaskInstance> getTDsTaskInstanceListByIn();

    List<TDsTaskInstance> getTDsTaskInstanceListByExists();


}
