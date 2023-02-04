package com.kuze.bigdata.study.utils;

import org.apache.spark.sql.types.*;
import scala.collection.JavaConverters;

import java.util.*;
import java.util.stream.Collectors;

public class StructTypeUtils {

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
        throw new Exception("不支持的 Spark DataType（" + dataType.typeName() +"）");
    }

    public static List<String> convertClickhouseSortColumnsToSqlListStr(StructType schema, String sortColumnsStr) throws Exception {
        String[] columns = sortColumnsStr.split(",");
        Set<String> set = Arrays.stream(columns).collect(Collectors.toSet());
        List<StructField> structFields = JavaConverters.seqAsJavaList(schema.toList());
        List<String> list = new ArrayList<>();
        for (StructField x : structFields) {
            String fieldName = x.name();
            if(set.contains(fieldName)){
                DataType dataType = x.dataType();
                String sqlDataType = null;
                sqlDataType = StructTypeUtils.convertSparkDataTypeToSqlDataType(dataType);
                list.add(fieldName + " " + sqlDataType);
            }
        }
        return list;
    }

    public static DataType convertClickhouseDataTypeToSparkDataType(String chDataType) throws Exception {
        switch (chDataType) {
            case "String":
                return DataTypes.StringType;
            case "UInt8":
                return DataTypes.IntegerType;
            case "UInt32":
                return DataTypes.LongType;
            case "UInt64":
                return DataTypes.LongType;
            case "Int8":
                return DataTypes.IntegerType;
            case "Int32":
                return DataTypes.LongType;
            case "Int64":
                return DataTypes.LongType;
            case "Bool":
                return DataTypes.BooleanType;
            case "Date":
                return DataTypes.DateType;
            case "DateTime":
                return DataTypes.DateType;
            case "Float32":
                return DataTypes.FloatType;
            case "Float64":
                return DataTypes.DoubleType;
            default:
                throw new Exception("不支持的 Clickhouse DataType（" + chDataType +"）");
        }
    }

    public static StructType convertClickhouseTableColumnsToSparkStructType(Map<String, String> columnsMap) {
        Set<Map.Entry<String, String>> entries = columnsMap.entrySet();
        List<StructField> list = new ArrayList<>();
        entries.forEach(x->{
            String fieldName = x.getKey();
            String fieldType = x.getValue();
            DataType dataType = null;
            try {
                dataType = StructTypeUtils.convertClickhouseDataTypeToSparkDataType(fieldType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            StructField structField = DataTypes.createStructField(fieldName, dataType, true, Metadata.empty());
            list.add(structField);
        });
        return new StructType(list.toArray(new StructField[0]));
    }

}
