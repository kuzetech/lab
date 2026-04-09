package com.funnydb.mutation;

import com.funnydb.mutation.model.MutationData;
import com.funnydb.mutation.model.MutationEvent;

import java.util.LinkedHashMap;
import java.util.Map;

final class TestData {
    private TestData() {
    }

    static MutationEvent event(String app, long time, String identify, Map<String, Object> properties) {
        MutationData data = new MutationData();
        data.setIdentify(identify);
        data.setDataLifecycle("0");
        data.setOperate("set");
        data.setTime(time);
        data.setProperties(properties);

        MutationEvent event = new MutationEvent();
        event.setApp(app);
        event.setType("UserMutation");
        event.setData(data);
        return event;
    }

    static Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            values.put((String) kv[i], kv[i + 1]);
        }
        return values;
    }
}
