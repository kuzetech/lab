package com.funnydb.mutation;

import com.funnydb.mutation.infra.JedisRedisMutationStore;
import com.funnydb.mutation.model.MutationData;
import com.funnydb.mutation.model.MutationEvent;
import com.funnydb.mutation.service.MutationApplyResult;
import com.funnydb.mutation.service.MutationEngine;
import com.funnydb.mutation.service.MutationEnvelope;
import com.funnydb.mutation.service.MutationStatus;
import com.funnydb.mutation.support.RedisBackedMutationProcessor;
import com.funnydb.mutation.support.TestcontainersEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MutationUnitTests {

    private final MutationEngine engine = new MutationEngine();
    private JedisRedisMutationStore store;
    private RedisBackedMutationProcessor processor;

    @Before
    public void setUp() {
        TestcontainersEnvironment.flushRedis();
        store = TestcontainersEnvironment.newRedisStore();
        processor = new RedisBackedMutationProcessor(engine, store);
    }

    @After
    public void tearDown() {
        if (store != null) {
            store.close();
        }
    }

    @Test
    public void shouldApplyAddAndBuildSnapshot() {
        MutationEnvelope envelope = processor.process(event("demo", "UserMutation", "u-1", "0", "add",
                1000L, mapOf("score", 2, "age", 1)), 2000L);

        assertEquals("demo:0:user:u-1", envelope.getRouting().getRedisKey());
        assertEquals("demo-flink-users", envelope.getRouting().getOutputTopic());
        assertEquals("u-1", envelope.getRouting().getOutputKey());
        assertEquals(MutationStatus.APPLIED, envelope.getApplyResult().getStatus());
        assertNumberEquals(2L, envelope.getSnapshot().get("score"));
        assertNumberEquals(1L, envelope.getSnapshot().get("age"));
        assertEquals(1000L, envelope.getSnapshot().get("#event_time"));
        assertEquals(2000L, envelope.getSnapshot().get("#updated_time"));
        assertEquals(1000L, envelope.getSnapshot().get("#created_time"));
        assertEquals("0", envelope.getSnapshot().get("#data_lifecycle"));
        assertEquals("u-1", envelope.getSnapshot().get("#user_id"));
    }

    @Test
    public void shouldIgnoreDuplicateOrOldEvent() {
        processor.process(event("demo", "UserMutation", "u-1", "0", "add", 1000L, mapOf("score", 2)), 2000L);

        MutationEnvelope duplicate = processor.process(
                event("demo", "UserMutation", "u-1", "0", "add", 1000L, mapOf("score", 5)),
                3000L);
        MutationEnvelope older = processor.process(
                event("demo", "UserMutation", "u-1", "0", "set", 999L, mapOf("name", "late")),
                4000L);

        assertEquals(MutationStatus.IGNORED_OLD_EVENT, duplicate.getApplyResult().getStatus());
        assertEquals(MutationStatus.IGNORED_OLD_EVENT, older.getApplyResult().getStatus());
        assertNumberEquals(2L, duplicate.getSnapshot().get("score"));
        assertNull(older.getSnapshot().get("name"));
        assertEquals(1000L, older.getSnapshot().get("#event_time"));
    }

    @Test
    public void shouldIgnoreOnlyTypeMismatchedFields() {
        processor.process(event("demo", "UserMutation", "u-1", "0", "set",
                1000L, mapOf("name", "alex", "score", 5)), 2000L);

        MutationEnvelope envelope = processor.process(event("demo", "UserMutation", "u-1", "0", "set",
                1001L, mapOf("name", 123, "score", 8, "city", "shanghai")), 3000L);

        MutationApplyResult result = envelope.getApplyResult();
        assertEquals(MutationStatus.INVALID_FIELD_TYPE, result.getStatus());
        assertEquals(1, result.getIgnoredFields().size());
        assertEquals("name", result.getIgnoredFields().get(0));
        assertEquals("alex", envelope.getSnapshot().get("name"));
        assertNumberEquals(8L, envelope.getSnapshot().get("score"));
        assertEquals("shanghai", envelope.getSnapshot().get("city"));
    }

    @Test
    public void shouldHonorSetOnceAndRetainNullsInSnapshot() {
        processor.process(event("demo", "DeviceMutation", "d-1", "7", "set",
                1000L, mapOf("model", null, "enabled", false)), 2000L);

        MutationEnvelope envelope = processor.process(event("demo", "DeviceMutation", "d-1", "7", "setOnce",
                1001L, mapOf("model", "ios", "enabled", true, "version", 17)), 3000L);

        assertEquals("ios", envelope.getSnapshot().get("model"));
        assertEquals(false, envelope.getSnapshot().get("enabled"));
        assertNumberEquals(17L, envelope.getSnapshot().get("version"));
        assertEquals("d-1", envelope.getSnapshot().get("#device_id"));
        assertEquals("7", envelope.getSnapshot().get("#data_lifecycle"));
    }

    private MutationEvent event(String app,
                                String type,
                                String identify,
                                String lifecycle,
                                String operate,
                                long time,
                                Map<String, Object> properties) {
        MutationData data = new MutationData();
        data.setIdentify(identify);
        data.setDataLifecycle(lifecycle);
        data.setOperate(operate);
        data.setTime(time);
        data.setProperties(properties);

        MutationEvent event = new MutationEvent();
        event.setApp(app);
        event.setType(type);
        event.setData(data);
        return event;
    }

    private Map<String, Object> mapOf(Object... kv) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            values.put((String) kv[i], kv[i + 1]);
        }
        return values;
    }

    private void assertNumberEquals(long expected, Object actual) {
        assertEquals(expected, ((Number) actual).longValue());
    }
}
