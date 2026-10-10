package com.atguigu.dga.meta.mapper;

import com.atguigu.dga.meta.bean.TableMetaInfoExtra;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 元数据表附加信息 Mapper 接口
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-28
 */
@Mapper
@DS("dga")
public interface TableMetaInfoExtraMapper extends BaseMapper<TableMetaInfoExtra> {

}
