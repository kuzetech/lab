import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.flink.FlinkCatalogFactory;
import org.apache.paimon.flink.sink.FlinkSinkBuilder;
import org.apache.paimon.flink.source.FlinkSourceBuilder;
import org.apache.paimon.options.Options;
import org.apache.paimon.table.Table;


public class DwdTrafficLogApp {

    public static void main(String[] args) throws Exception {

        //1准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //并行度
        env.setParallelism(2);

        //hdfs操作账号
        System.setProperty("HADOOP_USER_NAME", "root");

        //持久化checkpoint的配置
        env.enableCheckpointing(15000, CheckpointingMode.EXACTLY_ONCE);
        env.setStateBackend(new EmbeddedRocksDBStateBackend(true));
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage("hdfs://namenode:9000/flink/checkpoints/ods_log");
        checkpointConfig.setMinPauseBetweenCheckpoints(5000);
        checkpointConfig.setTolerableCheckpointFailureNumber(3);
        checkpointConfig.setCheckpointTimeout(60000);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        //2加载paimon为数据流
        //从catalog中获得表对象
        Options catalogOptions = new Options();
        catalogOptions.set("type", "paimon");
        catalogOptions.set("metastore", "hive");
        catalogOptions.set("uri", "thrift://hive-metastore:9083");
        catalogOptions.set("hive-conf-dir", "./src/main/resources/hive-site.xml");
        catalogOptions.set("warehouse", "hdfs://namenode:9000/paimon/hive");
        Catalog catalog = FlinkCatalogFactory.createPaimonCatalog(catalogOptions);
        Table sourceTable = catalog.getTable(Identifier.create("gesac_lake", "ods_log"));

        //获得数据流
        FlinkSourceBuilder sourceBuilder = new FlinkSourceBuilder(sourceTable).env(env);
        DataStream<Row> rowDataStream = sourceBuilder.buildForRow();
        //rowDataStream.executeAndCollect().forEachRemaining(System.out::println);

        //3分流标记
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

        //4把每个流写入到对应的paimon表中
        //提取table
        Table pageSinkTable = catalog.getTable(Identifier.create("gesac_lake", "dwd_traffic_page"));
        Table startSinkTable = catalog.getTable(Identifier.create("gesac_lake", "dwd_traffic_other"));

        //行类型准备
        DataType pageInputType =
                DataTypes.ROW(
                        DataTypes.FIELD("page", DataTypes.STRING()),
                        DataTypes.FIELD("dt", DataTypes.STRING()),
                        DataTypes.FIELD("ts", DataTypes.BIGINT())
                );
        DataType startInputType =
                DataTypes.ROW(
                        DataTypes.FIELD("event", DataTypes.STRING()),
                        DataTypes.FIELD("dt", DataTypes.STRING()),
                        DataTypes.FIELD("ts", DataTypes.BIGINT())
                );
        FlinkSinkBuilder pageSinkBuilder = new FlinkSinkBuilder(pageSinkTable).forRow(pageStream, pageInputType);
        FlinkSinkBuilder startSinkBuilder = new FlinkSinkBuilder(startSinkTable).forRow(sideStream, startInputType);

        pageSinkBuilder.build();
        startSinkBuilder.build();

        env.execute();
    }
}
