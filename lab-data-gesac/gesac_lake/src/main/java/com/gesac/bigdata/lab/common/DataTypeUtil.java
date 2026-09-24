package com.gesac.bigdata.lab.common;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;

public class DataTypeUtil {
    public static DataType getOdsLogEvent() {
        return DataTypes.ROW(
                DataTypes.FIELD("event", DataTypes.STRING()),
                DataTypes.FIELD("page", DataTypes.STRING()),
                DataTypes.FIELD("ts", DataTypes.BIGINT()),
                DataTypes.FIELD("dt", DataTypes.STRING())
        );
    }

    public static DataType getDwdPageEvent() {
        return DataTypes.ROW(
                DataTypes.FIELD("page", DataTypes.STRING()),
                DataTypes.FIELD("ts", DataTypes.BIGINT()),
                DataTypes.FIELD("dt", DataTypes.STRING())
        );
    }

    public static DataType getDwdOtherEvent() {
        return DataTypes.ROW(
                DataTypes.FIELD("event", DataTypes.STRING()),
                DataTypes.FIELD("ts", DataTypes.BIGINT()),
                DataTypes.FIELD("dt", DataTypes.STRING())
        );
    }
}
