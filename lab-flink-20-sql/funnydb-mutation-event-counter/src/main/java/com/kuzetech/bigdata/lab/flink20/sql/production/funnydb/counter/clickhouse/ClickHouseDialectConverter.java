package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter.clickhouse;

import org.apache.flink.connector.jdbc.core.database.dialect.AbstractDialectConverter;
import org.apache.flink.table.types.logical.RowType;

public class ClickHouseDialectConverter extends AbstractDialectConverter {
    public ClickHouseDialectConverter(RowType rowType) {
        super(rowType);
    }

    @Override
    public String converterName() {
        return "ClickHouse";
    }
}
