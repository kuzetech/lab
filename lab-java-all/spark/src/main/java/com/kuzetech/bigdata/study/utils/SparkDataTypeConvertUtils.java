package com.kuzetech.bigdata.study.utils;

import org.apache.spark.sql.types.*;

public class SparkDataTypeConvertUtils {

    public static String convertSparkDataTypeToSqlDataType(DataType dataType) throws Exception {
        if (dataType instanceof StringType) {
            return "STRING";
        }
        if (dataType instanceof IntegerType) {
            return "INT";
        }
        if (dataType instanceof FloatType) {
            return "FLOAT";
        }
        if (dataType instanceof LongType) {
            return "LONG";
        }
        if (dataType instanceof BooleanType) {
            return "BOOLEAN";
        }
        if (dataType instanceof DoubleType) {
            return "DOUBLE";
        }
        if (dataType instanceof DateType) {
            return "STRING";
        }
        if (dataType instanceof TimestampType) {
            return "STRING";
        }
        throw new Exception("不支持的 Spark DataType（" + dataType.typeName() + "）");
    }

}
