package com.gesac.bigdata.lab.log;

import com.gesac.bigdata.lab.common.CatalogUtil;
import com.gesac.bigdata.lab.common.EnvironmentUtil;
import com.gesac.bigdata.lab.common.PaimonUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.flink.sink.FlinkSinkBuilder;
import org.apache.paimon.flink.source.FlinkSourceBuilder;


public class Dws2Dwd_SplitStream {

    public static void main(String[] args) throws Exception {

        //1准备环境
        StreamExecutionEnvironment env = EnvironmentUtil.generateStreamExecutionEnvironment("log_ods_to_dwd");

        //2加载catalog
        Catalog catalog = CatalogUtil.generateHiveCatalog();

        //3获得数据流
        FlinkSourceBuilder sourceBuilder = PaimonUtil.generatePaimonSource(catalog, "gesac_lake", "ods_log", env);
        DataStream<Row> rowDataStream = sourceBuilder.buildForRow();

        //4分流标记
        OutputTag pageOutputTag = new OutputTag<Row>("page_tag") {
        };
        SingleOutputStreamOperator<Row> sideStream = rowDataStream.process(new ProcessFunction<Row, Row>() {
            @Override
            public void processElement(Row row, ProcessFunction<Row, Row>.Context context, Collector<Row> collector) throws Exception {
                String event = row.getFieldAs("event");
                String page = row.getFieldAs("page");
                Long ts = row.getFieldAs("ts");
                String dt = row.getFieldAs("dt");
                if ("page".equalsIgnoreCase(event)) {
                    Row pageRow = Row.of(page, dt, ts);
                    context.output(pageOutputTag, pageRow);
                } else {
                    collector.collect(row);
                }
            }
        });
        SideOutputDataStream<Row> pageStream = sideStream.getSideOutput(pageOutputTag);

        //5把流写入到对应的paimon表中
        FlinkSinkBuilder pageSinkBuilder = PaimonUtil.generateDwdPageSink(catalog, "gesac_lake", "dwd_traffic_page", pageStream);
        FlinkSinkBuilder otherSinkBuilder = PaimonUtil.generateDwdOtherSink(catalog, "gesac_lake", "dwd_traffic_other", sideStream);

        pageSinkBuilder.build();
        otherSinkBuilder.build();

        env.execute();
    }
}
