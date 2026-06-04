package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter.clickhouse;

import org.apache.flink.connector.jdbc.core.database.dialect.AbstractDialect;
import org.apache.flink.connector.jdbc.core.database.dialect.JdbcDialectConverter;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.RowType;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class ClickHouseDialect extends AbstractDialect {
    private static final Set<LogicalTypeRoot> SUPPORTED_TYPES = EnumSet.of(
            LogicalTypeRoot.CHAR,
            LogicalTypeRoot.VARCHAR,
            LogicalTypeRoot.BOOLEAN,
            LogicalTypeRoot.BINARY,
            LogicalTypeRoot.VARBINARY,
            LogicalTypeRoot.DECIMAL,
            LogicalTypeRoot.TINYINT,
            LogicalTypeRoot.SMALLINT,
            LogicalTypeRoot.INTEGER,
            LogicalTypeRoot.BIGINT,
            LogicalTypeRoot.FLOAT,
            LogicalTypeRoot.DOUBLE,
            LogicalTypeRoot.DATE,
            LogicalTypeRoot.TIME_WITHOUT_TIME_ZONE,
            LogicalTypeRoot.TIMESTAMP_WITHOUT_TIME_ZONE,
            LogicalTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE
    );

    @Override
    public JdbcDialectConverter getRowConverter(RowType rowType) {
        return new ClickHouseDialectConverter(rowType);
    }

    @Override
    public Optional<String> defaultDriverName() {
        return Optional.of("com.clickhouse.jdbc.ClickHouseDriver");
    }

    @Override
    public String dialectName() {
        return "ClickHouse";
    }

    @Override
    public String getLimitClause(long limit) {
        return "LIMIT " + limit;
    }

    @Override
    public Optional<String> getUpsertStatement(String tableName, String[] fieldNames, String[] uniqueKeyFields) {
        return Optional.empty();
    }

    @Override
    public Optional<Range> timestampPrecisionRange() {
        return Optional.of(Range.of(0, 9));
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return identifier;
    }

    @Override
    public Set<LogicalTypeRoot> supportedTypes() {
        return SUPPORTED_TYPES;
    }
}
