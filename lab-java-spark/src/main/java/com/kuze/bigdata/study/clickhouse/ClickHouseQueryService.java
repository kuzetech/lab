package com.kuze.bigdata.study.clickhouse;

import com.alibaba.fastjson.JSONObject;
import com.kuze.bigdata.study.streaming.updateBroadcast.ClickhouseBroadcastContent;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.clickhouse.BalancedClickhouseDataSource;
import ru.yandex.clickhouse.ClickHouseConnection;
import scala.collection.JavaConverters;

import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ClickHouseQueryService implements Serializable {

    private final static Logger logger = LoggerFactory.getLogger(ClickHouseQueryService.class);

    private static final String CLICKHOUSE_BALANCED_CONNECT_URL = "jdbc:clickhouse://localhost:8121,localhost:8122/system";
    public static final String CLICKHOUSE_DEST_DATABASE = "default";
    public static final String CLICKHOUSE_DEST_TABLE = "event_local";
    private static final String CLICKHOUSE_USERNAME = "default";
    private static final String CLICKHOUSE_PASSWORD = "";
    private static final String CLICKHOUSE_CLUSTER = "my";
    private static final String CLICKHOUSE_PORT = "8123";

    private static final String CLICKHOUSE_JDBC_URL_TEMPLATE = "jdbc:clickhouse://%hosts%:%port%/%database%?log_queries=1";
    private static final String CLICKHOUSE_INSERT_SQL_TEMPLATE = "INSERT INTO %s.%s (%s) VALUES (%s)";
    private static final String CLICKHOUSE_AVAILABLE_HOST_SQL_TEMPLATE = "select host_name from clusters where cluster = '%s'";
    private static final String CLICKHOUSE_TABLE_COLUMN_SQL_TEMPLATE = "select name, type from columns where table = '%s'";

    private transient ClickHouseConnection driverConn;

    public ClickHouseQueryService() throws SQLException {
        driverConn = createBalancedConnection();
    }

    public void closeDriverConn() throws SQLException {
        driverConn.close();
    }

    public ClickhouseBroadcastContent searchClickhouseBroadcastContent() throws SQLException {
        List<String> list = this.searchAvailableHost();
        Map<String, String> columnsMap = this.searchDestTableColumns();
        StructType structType = ClickHouseDataTypeConvertUtils.convertClickhouseTableColumnsToSparkStructType(columnsMap);
        ClickhouseBroadcastContent content = new ClickhouseBroadcastContent(structType, list);
        return content;
    }


    private String getConnectUrl(String host, String port, String database) {
        return CLICKHOUSE_JDBC_URL_TEMPLATE
                .replace("%hosts%", host)
                .replace("%port%", port)
                .replace("%database%", database);
    }

    private ClickHouseConnection createBalancedConnection() throws SQLException {
        BalancedClickhouseDataSource balancedDs = new BalancedClickhouseDataSource(CLICKHOUSE_BALANCED_CONNECT_URL)
                .scheduleActualization(60, TimeUnit.SECONDS);
        ClickHouseConnection conn = balancedDs.getConnection(CLICKHOUSE_USERNAME, CLICKHOUSE_PASSWORD);
        return conn;
    }

    private String createSearchAvailableHostStatementSql() {
        String sql = String.format(CLICKHOUSE_AVAILABLE_HOST_SQL_TEMPLATE, CLICKHOUSE_CLUSTER);
        return sql;
    }

    private List<String> searchAvailableHost() throws SQLException {
        String sql = createSearchAvailableHostStatementSql();
        Statement stmt = driverConn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<String> connectUrlList = new ArrayList<>();
        while (rs.next()) {
            String host = rs.getString("host_name");
            String connectUrl = getConnectUrl(host, CLICKHOUSE_PORT, CLICKHOUSE_DEST_DATABASE);
            connectUrlList.add(connectUrl);
        }
        return connectUrlList;
    }

    private String createSearchDestTableColumnsStructTypeStatementSql() {
        String sql = String.format(CLICKHOUSE_TABLE_COLUMN_SQL_TEMPLATE, CLICKHOUSE_DEST_TABLE);
        return sql;
    }


    private Map<String, String> searchDestTableColumns() throws SQLException {
        String sql = createSearchDestTableColumnsStructTypeStatementSql();
        Statement stmt = driverConn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        Map<String, String> map = new HashMap<>();
        List<String> fieldNameAndTypeList = new ArrayList<>();
        while (rs.next()) {
            String fieldName = rs.getString("name");
            String fieldType = rs.getString("type");
            map.put(fieldName, fieldType);
            fieldNameAndTypeList.add(fieldName + " " + fieldType);
        }
        String fieldsStr = fieldNameAndTypeList.stream().collect(Collectors.joining(","));
        logger.info("目标表 {}.{} 获取到的最新表结构为: {}", CLICKHOUSE_DEST_DATABASE, CLICKHOUSE_DEST_TABLE, fieldsStr);
        return map;
    }

    private Connection createWorkerConnection(String url) throws Exception {
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        Connection conn = DriverManager.getConnection(url, CLICKHOUSE_USERNAME, CLICKHOUSE_PASSWORD);
        return conn;
    }


    private String createInsertStatementSql(StructType schema) {
        List<StructField> structFields = JavaConverters.<StructField>seqAsJavaList(schema.toList());
        String columnsString = structFields.stream().map(x -> x.name()).collect(Collectors.joining(","));
        String valuesString = structFields.stream().map(x -> "?").collect(Collectors.joining(","));

        String sql = String.format(
                CLICKHOUSE_INSERT_SQL_TEMPLATE,
                CLICKHOUSE_DEST_DATABASE,
                CLICKHOUSE_DEST_TABLE,
                columnsString,
                valuesString);
        return sql;
    }

    public Long batchInsert(Iterator<Row> input, StructType schema, String connectUrl) throws Exception {
        Connection connection = createWorkerConnection(connectUrl);
        String sql = createInsertStatementSql(schema);
        PreparedStatement statement = connection.prepareStatement(sql);
        List<StructField> structFields = JavaConverters.<StructField>seqAsJavaList(schema.toList());

        Long count = 0L;
        Integer jsonStrIndex = 0;
        if(input.hasNext()){
            Row firstRow = input.next();
            jsonStrIndex = firstRow.fieldIndex("value");
            statementAddBatch(schema, statement, structFields, jsonStrIndex, firstRow);
            count = count + 1;
        }

        while (input.hasNext()) {
            Row row = input.next();
            statementAddBatch(schema, statement, structFields, jsonStrIndex, row);
            count = count + 1;
        }

        statement.executeBatch();
        connection.close();
        return count;
    }

    private void statementAddBatch(
            StructType schema,
            PreparedStatement statement,
            List<StructField> structFields,
            Integer jsonStrIndex,
            Row row) throws SQLException {
        String jsonStr = row.getString(jsonStrIndex);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        for (StructField field : structFields) {
            String fieldName = field.name();
            int fieldIdx = schema.fieldIndex(fieldName);
            Object fieldVal = jsonObject.get(fieldName);
            statement.setObject(fieldIdx + 1, fieldVal);
        }
        statement.addBatch();
    }


}