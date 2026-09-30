package com.atguigu.dga.governance.service;

import com.atguigu.dga.governance.bean.TDsTaskDefinition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-09-02
 */
public interface TDsTaskDefinitionService extends IService<TDsTaskDefinition> {

    List<TDsTaskDefinition>  getTDsTaskDefinitionList();
}
