package com.kuze.bigdata.kstreams.funnypipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuze.bigdata.kstreams.funnypipe.metadata.FileMetadataFetcher;
import com.kuze.bigdata.kstreams.funnypipe.utils.JsonUtil;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class FunnyDBProcessor implements Processor<byte[], EnrichedEvent, byte[], DestMessage> {

    private static final Logger log = LoggerFactory.getLogger(FunnyDBProcessor.class);

    private ProcessorContext<byte[], DestMessage> context;
    private KeyValueStore<byte[], EnrichedEvent> kvStore;
    private FileMetadataFetcher fetcher;
    private JedisPool pool;

    public FunnyDBProcessor(FileMetadataFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public void init(ProcessorContext<byte[], DestMessage> context) {
        this.context = context;
        this.pool = new JedisPool("localhost", 6379);
        kvStore = context.getStateStore("cache");

        context.schedule(Duration.ofSeconds(3), PunctuationType.WALL_CLOCK_TIME, timestamp -> {
            if (kvStore.approximateNumEntries() <= 0) {
                return;
            }
            try (
                    final KeyValueIterator<byte[], EnrichedEvent> iter = kvStore.all();
                    Jedis jedis = pool.getResource()
            ) {
                List<EnrichedEvent> list = new ArrayList<>();
                Set<String> keys = new HashSet<>();
                while (iter.hasNext()) {
                    KeyValue<byte[], EnrichedEvent> entry = iter.next();
                    kvStore.delete(entry.key);
                    EnrichedEvent enrichedEvent = entry.value;
                    list.add(enrichedEvent);
                    keys.add(enrichedEvent.getApp());
                }
                String[] distinctKeyArray = keys.toArray(new String[0]);
                List<String> resultList = jedis.mget(distinctKeyArray);
                Map<String, String> resultMap = new HashMap<>();
                for (int i = 0; i < distinctKeyArray.length; i++) {
                    resultMap.put(distinctKeyArray[i], resultList.get(i));
                }
                for (EnrichedEvent enrichedEvent : list) {
                    String enrichContent = resultMap.get(enrichedEvent.getApp());
                    enrichedEvent.getData().getData().put("enrichContent", enrichContent);
                    context.forward(new Record<>(null, new DestMessage("test", JsonUtil.mapper.writeValueAsBytes(enrichedEvent.getData())), timestamp));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void process(Record<byte[], EnrichedEvent> record) {
        kvStore.put(record.value().getLogId().getBytes(StandardCharsets.UTF_8), record.value());
    }

    @Override
    public void close() {
        this.pool.close();
    }
}
