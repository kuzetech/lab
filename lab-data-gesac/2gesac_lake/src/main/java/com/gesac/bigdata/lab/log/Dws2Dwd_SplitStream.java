package com.gesac.bigdata.lab.log;

import com.gesac.bigdata.lab.common.CatalogUtil;
import com.gesac.bigdata.lab.common.EnvironmentUtil;
import com.gesac.bigdata.lab.common.PaimonUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.types.Row;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.flink.sink.FlinkSinkBuilder;
import org.apache.paimon.flink.source.FlinkSourceBuilder;


public class Dws2Dwd_SplitStream {

    private static final RowTypeInfo DWD_PAGE_ROW_TYPE = new RowTypeInfo(
            new TypeInformation[]{Types.STRING, Types.LONG, Types.STRING},
            new String[]{"page", "ts", "dt"}
    );

    private static final RowTypeInfo DWD_OTHER_ROW_TYPE = new RowTypeInfo(
            new TypeInformation[]{Types.STRING, Types.LONG, Types.STRING},
            new String[]{"event", "ts", "dt"}
    );

    public static void main(String[] args) throws Exception {

        //1准备环境
        StreamExecutionEnvironment env = EnvironmentUtil.generateStreamExecutionEnvironment("log_ods_to_dwd");
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2加载catalog
        Catalog catalog = CatalogUtil.generateHiveCatalog();
        createDwdTables(tableEnv);

        //3获得数据流
        FlinkSourceBuilder sourceBuilder = PaimonUtil.generatePaimonSource(catalog, "gesac_lake", "ods_log", env);
        DataStream<Row> rowDataStream = sourceBuilder.buildForRow();

        //4分流标记
        OutputTag<Row> pageOutputTag = new OutputTag<Row>("page_tag", DWD_PAGE_ROW_TYPE) {
        };
        SingleOutputStreamOperator<Row> sideStream = rowDataStream.process(new ProcessFunction<Row, Row>() {
            @Override
            public void processElement(Row row, ProcessFunction<Row, Row>.Context context, Collector<Row> collector) throws Exception {
                String event = row.getFieldAs("event");
                String page = row.getFieldAs("page");
                Long ts = row.getFieldAs("ts");
                String dt = row.getFieldAs("dt");
                if ("page".equalsIgnoreCase(event)) {
                    Row pageRow = Row.of(page, ts, dt);
                    context.output(pageOutputTag, pageRow);
                } else {
                    Row otherRow = Row.of(event, ts, dt);
                    collector.collect(otherRow);
                }
            }
        }).returns(DWD_OTHER_ROW_TYPE);
        SideOutputDataStream<Row> pageStream = sideStream.getSideOutput(pageOutputTag);

        //5把流写入到对应的paimon表中
        FlinkSinkBuilder pageSinkBuilder = PaimonUtil.generateDwdPageSink(catalog, "gesac_lake", "dwd_traffic_page", pageStream);
        FlinkSinkBuilder otherSinkBuilder = PaimonUtil.generateDwdOtherSink(catalog, "gesac_lake", "dwd_traffic_other", sideStream);

        pageSinkBuilder.build();
        otherSinkBuilder.build();

        env.execute();
    }

    private static void createDwdTables(StreamTableEnvironment tableEnv) {
        tableEnv.executeSql("""
                CREATE CATALOG hive_catalog WITH (
                    'type' = 'paimon',
                    'metastore' = 'hive',
                    'uri' = 'thrift://localhost:9083',
                    'hive-conf-dir' = 'src/main/resources',
                    'warehouse' = 'hdfs://namenode:9000/paimon/hive'
                )
                """);

        tableEnv.executeSql("""
                CREATE DATABASE IF NOT EXISTS hive_catalog.gesac_lake
                """);

        tableEnv.executeSql("""
                CREATE TABLE IF NOT EXISTS hive_catalog.gesac_lake.dwd_traffic_page (
                    `page` STRING,
                    `ts` BIGINT,
                    `dt` STRING
                ) PARTITIONED BY (`dt`) WITH (
                    'bucket-key' = 'page',
                    'bucket' = '2',
                    'path' = 'hdfs://namenode:9000/paimon/hive/gesac_lake.db/dwd_traffic_page',
                    'sink.parallelism' = '2',
                    'metastore.partitioned-table' = 'true'
                )
                """);

        tableEnv.executeSql("""
                CREATE TABLE IF NOT EXISTS hive_catalog.gesac_lake.dwd_traffic_other (
                    `event` STRING,
                    `ts` BIGINT,
                    `dt` STRING
                ) PARTITIONED BY (`dt`) WITH (
                    'bucket-key' = 'event',
                    'bucket' = '2',
                    'path' = 'hdfs://namenode:9000/paimon/hive/gesac_lake.db/dwd_traffic_other',
                    'sink.parallelism' = '2',
                    'metastore.partitioned-table' = 'true'
                )
                """);
    }
}
