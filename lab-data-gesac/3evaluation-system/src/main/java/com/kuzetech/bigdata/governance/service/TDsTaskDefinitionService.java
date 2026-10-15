package com.kuzetech.bigdata.governance.service;

import com.kuzetech.bigdata.governance.bean.TDsTaskDefinition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kuze
 * @since 2024-09-02
 */
public interface TDsTaskDefinitionService extends IService<TDsTaskDefinition> {

    List<TDsTaskDefinition>  getTDsTaskDefinitionList();
}
