package com.atguigu.dga.governance.service.impl;

import com.atguigu.dga.constant.DgaConstant;
import com.atguigu.dga.governance.assessor.Assessor;
import com.atguigu.dga.governance.bean.*;
import com.atguigu.dga.governance.mapper.GovernanceAssessDetailMapper;
import com.atguigu.dga.governance.service.GovernanceAssessDetailService;
import com.atguigu.dga.governance.service.GovernanceMetricService;
import com.atguigu.dga.governance.service.TDsTaskDefinitionService;
import com.atguigu.dga.governance.service.TDsTaskInstanceService;
import com.atguigu.dga.meta.bean.TableMetaInfo;
import com.atguigu.dga.meta.mapper.TableMetaInfoMapper;
import com.atguigu.dga.meta.service.TableMetaInfoExtraService;
import com.atguigu.dga.meta.service.TableMetaInfoService;
import com.atguigu.dga.util.SpringBeanProvider;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 治理考评结果明细 服务实现类
 * </p>
 *
 * @author weiyunhui
 * @since 2024-08-30
 */
@Service
@DS("dga")
public class GovernanceAssessDetailServiceImpl extends ServiceImpl<GovernanceAssessDetailMapper, GovernanceAssessDetail> implements GovernanceAssessDetailService {

    @Autowired
    TableMetaInfoService tableMetaInfoService ;

    @Autowired
    TableMetaInfoExtraService tableMetaInfoExtraService ;

    @Autowired
    TableMetaInfoMapper tableMetaInfoMapper ;

    @Autowired
    GovernanceMetricService governanceMetricService ;

    @Autowired
    SpringBeanProvider springBeanProvider ;

    @Autowired
    TDsTaskDefinitionService tDsTaskDefinitionService;
    @Autowired
    TDsTaskInstanceService tDsTaskInstanceService ;

    //线程池
    ThreadPoolExecutor executor =
            new ThreadPoolExecutor(20, 20 , 60 , TimeUnit.SECONDS ,  new LinkedBlockingDeque<>(1600)) ;


    /**
     * 主考评方法
     * @param assessDate
     *
     * 考评思想:  每张表， 每个指标， 逐一进行考评
     *
     * 步骤:
     *    0. 清理考评日期当天的结果（幂等处理）
     *
     *    1. 读取到所有待考评的表
     *         // 方式一：
     *         // 1.1 先读取table_meta_info表中的当前考评日期对应的数据
     *         List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.list(
     *                 new QueryWrapper<TableMetaInfo>()
     *                         .eq("assess_date", assessDate)
     *         );
     *         // 1.2 迭代每个TableMetaInfo对象，获取到表名，库名，
     *         //     再到table_meta_info_extra表中读取对应的TableMetaInfoExtra， 并封装到TableMetaInfo对象中
     *         for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
     *             TableMetaInfoExtra tableMetaInfoExtra = tableMetaInfoExtraService.getOne(
     *                     new QueryWrapper<TableMetaInfoExtra>()
     *                             .eq("schema_name", tableMetaInfo.getSchemaName())
     *                             .eq("table_name", tableMetaInfo.getTableName())
     *             );
     *             tableMetaInfo.setTableMetaInfoExtra(tableMetaInfoExtra);
     *         }
     *
     *         // 方式二：
     *         // 1.1 先读取table_meta_info表中的当前考评日期对应的数据
     *         List<TableMetaInfo> tableMetaInfoList = tableMetaInfoService.list(
     *                 new QueryWrapper<TableMetaInfo>()
     *                         .eq("assess_date", assessDate)
     *         );
     *         // 1.2 读取table_meta_info_extra表中所有的数据， 封装到集合中
     *         List<TableMetaInfoExtra> tableMetaInfoExtraList = tableMetaInfoExtraService.list();
     *         // 处理成Map结构
     *         Map<String, TableMetaInfoExtra> tableMetaInfoExtraMap = new HashMap<>();
     *         tableMetaInfoExtraList.forEach( te -> tableMetaInfoExtraMap.put(te.getSchemaName()+"."+te.getTableName() , te ));
     *
     *         // 1.3 迭代集合， 将TableMetaInfoExtra补充到TableMetaInfo中
     *         tableMetaInfoList.forEach( ti -> ti.setTableMetaInfoExtra( tableMetaInfoExtraMap.get(ti.getSchemaName()+"."+ti.getTableName())));
     *
     *         // 方式三：
     *         // 通过Join Sql查询 两张表的数据， 基于MyBatis 原生的 xml映射方式，将结果集映射到TableMetaInfo对象中
     *         List<TableMetaInfo> tableMetaInfoList = tableMetaInfoMapper.selectAllTableMetaInfoWithExtra(assessDate);
     *
     *    2. 读取到所有的指标
     *
     *    3. 读取DS中的任务定义 和 任务实例
     *
     *    4. 执行考评
     *       // 每张表， 每个指标， 逐一进行考评
     *                 // 将每个指标设计成一个具体的类， 类中包含考评的方法
     *                 // 每个考评器长得都一样， 可以考虑抽取统一的父类。
     *                 // 可以在父类中抽取每个考评器通用的代码， 减少代码重复
     *                 // 可以在父类中控制一些标准的流程，让子类按照标准的流程工作
     *                 // 结论: 考评器父类(Assessor), 统一控制考评的流程， 每个具体的子考评器重点关注查找问题的细节
     *
     *                 // 如果通过指标对应到考评器?
     *                 // 开发原则: 开闭原则， 对功能的新增开放， 对功能的修改关闭.
     *                 // 方式一: 反射
     *                 // 约定:  1.考评器的类名 由 指标编码来命名
     *                 //           TABLE_TEC_OWNER => TableTecOwnerAssessor
     *                 //           TABLE_BUSI_OWNER => TableBusiOwnerAssessor
     *                 //       2. 考评器所在包名由 指标的类型来命名
     *                 //           SPEC => spec
     *                 //           STORAGE => storage
     *                 //           CALC => calc
     *                 //           QUALITY => quality
     *                 //           SECURITY => security
     *
     *                 //       3. 基包:  com.atguigu.dga.governance.assessor
     *
     *                 String basePackage = "com.atguigu.dga.governance.assessor" ;
     *                 String governanceMetricPackage  = governanceMetric.getGovernanceType().toLowerCase();
     *                 String className = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL , governanceMetric.getMetricCode())+"Assessor";
     *                 String fullClassName = basePackage + "." + governanceMetricPackage + "." + className ;
     *                 try {
     *                     Class<?> clsObj = Class.forName(fullClassName);
     *                     Assessor assessor = (Assessor)clsObj.newInstance();
     *                     assessor.doAssess();
     *                 } catch (Exception e) {
     *                     throw new RuntimeException(e);
     *                 }
     *
     *                 // 方式二: 使用Spring容器来实现
     *                 //       约定:  每个指标对应的考评器，管理到Spring容器中时 ，需要使用指标的编码来命名。
     *                 //             指标: 是否有技术OWNER(TABLE_TEC_OWNER) => TableTecOwnerAssessor  => @Component("TABLE_TEC_OWNER")
     *                 String metricCode = governanceMetric.getMetricCode();
     *                 //获取考评器对象
     *                 Assessor assessor = springBeanProvider.getBean(metricCode, Assessor.class);
     *                 //开始考评
     *                 assessor.doAssess();
     *
     *    5. 将考评结果写到数据库表中
     */
    @Override
    public void mainAssess(String assessDate) {
        //0. 清理考评日期当天的结果（幂等处理）
        remove(
                new QueryWrapper<GovernanceAssessDetail>()
                        .eq("assess_date" , assessDate)
        );

        // 1. 读取到所有待考评的表
        // 方式三：
        // 通过Join Sql查询 两张表的数据， 基于MyBatis 原生的 xml映射方式，将结果集映射到TableMetaInfo对象中
        List<TableMetaInfo> tableMetaInfoList = tableMetaInfoMapper.selectAllTableMetaInfoWithExtra(assessDate);

        // System.out.println(tableMetaInfoList);

        // 2.读取到所有的指标
        List<GovernanceMetric> governanceMetricList = governanceMetricService.list(
                new QueryWrapper<GovernanceMetric>()
                        .eq("is_disabled", "0")
        );

        // System.out.println(governanceMetricList);

        //ArrayList<GovernanceAssessDetail> governanceAssessDetails = new ArrayList<>(tableMetaInfoList.size() * governanceMetricList.size());
        ArrayList<CompletableFuture<GovernanceAssessDetail>> futures =
                new ArrayList<>(tableMetaInfoList.size() * governanceMetricList.size());



        // 3. 读取DS中的任务定义 和 任务实例
        // 任务定义
        List<TDsTaskDefinition> tDsTaskDefinitionList = tDsTaskDefinitionService.getTDsTaskDefinitionList();
        //System.out.println(tDsTaskDefinitionList);
        //转换成Map集合
        Map<String, TDsTaskDefinition> tDsTaskDefinitionMap = new HashMap<>( tDsTaskDefinitionList.size() );
        tDsTaskDefinitionList.forEach( tDsTaskDefinition -> tDsTaskDefinitionMap.put( tDsTaskDefinition.getName()  , tDsTaskDefinition ));

        // 任务实例
        // 正常情况下， 需要提取考评日期当天对应的最后成功的任务实例.
        // 目前直接提取每张表对应的实例中， 最后成功的实例
        List<TDsTaskInstance> tDsTaskInstanceList =
                //tDsTaskInstanceService.getTDsTaskInstanceListByJoin();
                //tDsTaskInstanceService.getTDsTaskInstanceListByIn();
                tDsTaskInstanceService.getTDsTaskInstanceListByExists();

        //System.out.println(tDsTaskInstanceList);

        //转换成Map集合
        Map<String, TDsTaskInstance> tDsTaskInstanceMap = new HashMap<>( tDsTaskInstanceList.size() );
        tDsTaskInstanceList.forEach( tDsTaskInstance -> tDsTaskInstanceMap.put( tDsTaskInstance.getName()  , tDsTaskInstance ));


        long start = System.currentTimeMillis();

        // 4. 执行考评
        //每张表
        for (TableMetaInfo tableMetaInfo : tableMetaInfoList) {
            //每个指标
            for (GovernanceMetric governanceMetric : governanceMetricList) {
                //处理白名单
                String skipAssessTables = governanceMetric.getSkipAssessTables();
                if(skipAssessTables != null && !skipAssessTables.trim().isEmpty()){
                    List<String> skipList = Arrays.asList(skipAssessTables.split(","));
                    if( skipList.contains( tableMetaInfo.getTableName() )){
                        continue;
                    }
                }

                // 如果通过指标对应到考评器?
                // 方式二: 使用Spring容器来实现
                //       约定:  每个指标对应的考评器，管理到Spring容器中时 ，需要使用指标的编码来命名。
                //             指标: 是否有技术OWNER(TABLE_TEC_OWNER) => TableTecOwnerAssessor  => @Component("TABLE_TEC_OWNER")
                String metricCode = governanceMetric.getMetricCode();
                //获取考评器对象
                Assessor assessor = springBeanProvider.getBean(metricCode, Assessor.class);

                //封装考评参数
                //传统写法
                //AssessParam assessParam = new AssessParam();
                //assessParam.setTableMetaInfo( tableMetaInfo );
                //assessParam.setGovernanceMetric(governanceMetric);

                //建造者写法
                AssessParam assessParam =
                        AssessParam.builder()
                                .tableMetaInfo(tableMetaInfo)
                                .governanceMetric(governanceMetric)
                                .assessDate(assessDate)
                                .tableMetaInfoList(tableMetaInfoList)
                                .tDsTaskDefinition( tDsTaskDefinitionMap.get( tableMetaInfo.getSchemaName()+"."+tableMetaInfo.getTableName() ))
                                .tDsTaskInstance( tDsTaskInstanceMap.get( tableMetaInfo.getSchemaName()+"."+tableMetaInfo.getTableName()))
                                .build();

                //开始考评
                //GovernanceAssessDetail governanceAssessDetail = assessor.doAssess( assessParam );
                //攒批
                //governanceAssessDetails.add( governanceAssessDetail) ;

                //将每个考评过程处理成一个异步任务
                CompletableFuture<GovernanceAssessDetail> future = CompletableFuture.supplyAsync(
                        () -> {
                            return assessor.doAssess(assessParam);
                        },
                        executor
                );

                futures.add( future ) ;

            }
        }

        //执行异步， 集结结果
        List<GovernanceAssessDetail> governanceAssessDetails
                = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        long end = System.currentTimeMillis();

        System.out.println("并行考评总耗时: " + ( end - start ));


        // 5. 将考评结果写到数据库表中
        saveBatch(governanceAssessDetails) ;
    }
}
