package com.kuzetech.bigdata.lab.flink20.sql.production.funnydb.counter.clickhouse;

import org.apache.flink.connector.jdbc.core.database.JdbcFactory;
import org.apache.flink.connector.jdbc.core.database.catalog.JdbcCatalog;
import org.apache.flink.connector.jdbc.core.database.dialect.JdbcDialect;

public class ClickHouseFactory implements JdbcFactory {
    @Override
    public boolean acceptsURL(String url) {
        return url != null && url.startsWith("jdbc:clickhouse:");
    }

    @Override
    public JdbcDialect createDialect() {
        return new ClickHouseDialect();
    }

    @Override
    public JdbcCatalog createCatalog(
            ClassLoader classLoader,
            String catalogName,
            String defaultDatabase,
            String username,
            String pwd,
            String baseUrl) {
        throw new UnsupportedOperationException("ClickHouse catalog is not supported in this demo");
    }
}
