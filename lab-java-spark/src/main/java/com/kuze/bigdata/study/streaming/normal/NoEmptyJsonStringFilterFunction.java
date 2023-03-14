package com.kuze.bigdata.study.streaming.normal;

import com.kuze.bigdata.study.utils.JsonUtils;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Row;

import java.io.Serializable;

public class NoEmptyJsonStringFilterFunction implements FilterFunction<Row>, Serializable {

    @Override
    public boolean call(Row row) throws Exception {
        String jsonStr = row.getString(row.fieldIndex("value"));
        return JsonUtils.isJsonString(jsonStr);
    }

}
