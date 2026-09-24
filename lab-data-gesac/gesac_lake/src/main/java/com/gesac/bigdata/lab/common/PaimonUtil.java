package com.gesac.bigdata.lab.common;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.flink.sink.FlinkSinkBuilder;
import org.apache.paimon.flink.source.FlinkSourceBuilder;
import org.apache.paimon.table.Table;

public class PaimonUtil {
    public static FlinkSourceBuilder generatePaimonSource(
            Catalog catalog,
            String db,
            String table,
            StreamExecutionEnvironment env) throws Catalog.TableNotExistException {
        Table sourceTable = catalog.getTable(Identifier.create(db, table));
        return new FlinkSourceBuilder(sourceTable).env(env);
    }

    public static FlinkSinkBuilder generatePaimonSink(
            Catalog catalog,
            String db,
            String table,
            DataStream<Row> sinkStream,
            DataType sinkStreamDataType) throws Catalog.TableNotExistException {
        Table sinkTable = catalog.getTable(Identifier.create(db, table));
        return new FlinkSinkBuilder(sinkTable).forRow(sinkStream, sinkStreamDataType);
    }

    public static FlinkSinkBuilder generateOdsLogEventSink(
            Catalog catalog,
            String db,
            String table,
            DataStream<Row> sinkStream
    ) throws Catalog.TableNotExistException {
        return generatePaimonSink(catalog, db, table, sinkStream, DataTypeUtil.getOdsLogEvent());
    }

    public static FlinkSinkBuilder generateDwdPageSink(
            Catalog catalog,
            String db,
            String table,
            DataStream<Row> sinkStream
    ) throws Catalog.TableNotExistException {
        return generatePaimonSink(catalog, db, table, sinkStream, DataTypeUtil.getDwdPageEvent());
    }

    public static FlinkSinkBuilder generateDwdOtherSink(
            Catalog catalog,
            String db,
            String table,
            DataStream<Row> sinkStream
    ) throws Catalog.TableNotExistException {
        return generatePaimonSink(catalog, db, table, sinkStream, DataTypeUtil.getDwdOtherEvent());
    }
}
