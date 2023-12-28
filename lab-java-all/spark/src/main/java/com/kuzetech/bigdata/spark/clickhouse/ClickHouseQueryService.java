package com.kuzetech.bigdata.spark.clickhouse;

import org.apache.spark.sql.catalyst.InternalRow;
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

    private static final String CLICKHOUSE_JDBC_URL_TEMPLATE = "jdbc:clickhouse://%hosts%:%port%/%database%?log_queries=1";
    private static final String CLICKHOUSE_INSERT_SQL_TEMPLATE = "INSERT INTO %s.%s (%s) SETTINGS insert_deduplication_token = '%s' VALUES (%s)";
    private static final String CLICKHOUSE_AVAILABLE_HOST_SQL_TEMPLATE = "select host_name from clusters where cluster = '%s'";
    private static final String CLICKHOUSE_TABLE_COLUMN_SQL_TEMPLATE = "select name, type from columns where table = '%s'";

    private transient ClickHouseConnection conn;
    private ClickHouseQueryConfig config;

    public ClickHouseQueryService(ClickHouseQueryConfig config, Boolean createConnect) throws SQLException {
        this.config = config;
        if (createConnect) {
            conn = createBalancedConnection();
        }
    }

    public void closeDriverConn() throws SQLException {
        conn.close();
    }

    public List<String> searchAvailableServer() throws SQLException {
        List<String>  list = this.searchAvailableHost(config.getDatabase());
        logger.error("目标表为 {}.{} 的流程序从 clickhouse 服务中获取最新的可写入服务器连接信息，具体为 {}",
                config.getDatabase(),
                config.getTable(),
                list.toString());
        return list;
    }

    public StructType searchDestTableStructType() throws SQLException {
        Map<String, String> columnsMap = this.searchDestTableColumns(config.getDatabase(), config.getTable());
        StructType structType = ClickHouseDataTypeConvertUtils.convertClickhouseTableColumnsToSparkStructType(columnsMap);
        return structType;
    }


    private String getConnectUrl(String host, String port, String database) {
        return CLICKHOUSE_JDBC_URL_TEMPLATE
                .replace("%hosts%", host)
                .replace("%port%", port)
                .replace("%database%", database);
    }

    private ClickHouseConnection createBalancedConnection() throws SQLException {
        BalancedClickhouseDataSource balancedDs = new BalancedClickhouseDataSource(config.getConnectUrl())
                .scheduleActualization(60, TimeUnit.SECONDS);
        ClickHouseConnection conn = balancedDs.getConnection(config.getUser(), config.getPassword());
        return conn;
    }

    private String createSearchAvailableHostStatementSql() {
        String sql = String.format(CLICKHOUSE_AVAILABLE_HOST_SQL_TEMPLATE, config.getCluster());
        return sql;
    }

    private List<String> searchAvailableHost(String destDatabase) throws SQLException {
        String sql = createSearchAvailableHostStatementSql();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        List<String> connectUrlList = new ArrayList<>();
        while (rs.next()) {
            String host = rs.getString("host_name");
            String connectUrl = getConnectUrl(host, config.getPort(), destDatabase);
            connectUrlList.add(connectUrl);
        }
        return connectUrlList;
    }

    private String createSearchDestTableColumnsStructTypeStatementSql(String destTable) {
        String sql = String.format(CLICKHOUSE_TABLE_COLUMN_SQL_TEMPLATE, destTable);
        return sql;
    }


    private Map<String, String> searchDestTableColumns(String destDatabase, String destTable) throws SQLException {
        String sql = createSearchDestTableColumnsStructTypeStatementSql(destTable);
        Statement stmt = conn.createStatement();
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
        logger.info("目标表 {}.{} 获取到的最新表结构为: {}", destDatabase, destTable, fieldsStr);
        return map;
    }

    private Connection createWorkerConnection(String url) throws SQLException, ClassNotFoundException {
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        Connection conn = DriverManager.getConnection(url, config.getUser(), config.getPassword());
        return conn;
    }


    private String createInsertStatementSql(StructType schema, String batchIndex) {
        List<StructField> structFields = JavaConverters.seqAsJavaList(schema.toList());
        String columnsString = structFields.stream().map(x -> x.name()).collect(Collectors.joining(","));
        String valuesString = structFields.stream().map(x -> "?").collect(Collectors.joining(","));

        String sql = String.format(
                CLICKHOUSE_INSERT_SQL_TEMPLATE,
                config.getDatabase(),
                config.getTable(),
                columnsString,
                batchIndex,
                valuesString);
        return sql;
    }

    public Long batchInsert(
            Iterator<InternalRow> input,
            StructType rowSchema,
            StructType schema,
            String connectUrl,
            String batchIndex
    ) throws SQLException, ClassNotFoundException, InsertErrorException {
        Connection connection = createWorkerConnection(connectUrl);
        String sql = createInsertStatementSql(schema, batchIndex);
        PreparedStatement statement = connection.prepareStatement(sql);
        List<StructField> structFields = JavaConverters.seqAsJavaList(schema.toList());

        Long count = 0L;

        while (input.hasNext()) {
            InternalRow row = input.next();
            statementAddBatch(rowSchema, schema, statement, structFields, row);
            count = count + 1;
        }

        statement.executeBatch();
        connection.close();
        return count;
    }

    private void statementAddBatch(
            StructType rowSchema,
            StructType schema,
            PreparedStatement statement,
            List<StructField> structFields,
            InternalRow row) throws SQLException, InsertErrorException {
        for (StructField field : structFields) {
            String fieldName = field.name();
            int fieldIdx = schema.fieldIndex(fieldName);
            int rowIdx = rowSchema.fieldIndex(fieldName);
            Object fieldVal = row.get(rowIdx, rowSchema.apply(rowIdx).dataType());
            if(fieldVal == null){
                throw new InsertErrorException("本批次数据中 " + fieldName + " 字段必须存在");
            }
            statement.setObject(fieldIdx + 1, fieldVal);
        }
        statement.addBatch();
    }


}