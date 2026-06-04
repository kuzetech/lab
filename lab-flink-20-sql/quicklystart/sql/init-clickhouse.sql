CREATE DATABASE IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.record
(
    kafka_topic     String,
    kafka_partition Int32,
    kafka_offset    Int64,
    user_id         Nullable(String),
    event_time      Nullable(Int64),
    payload         String,
    ingested_at     DateTime64(3)
)
ENGINE = ReplacingMergeTree
ORDER BY (kafka_topic, kafka_partition, kafka_offset);

CREATE TABLE IF NOT EXISTS app.counter
(
    app          String,
    window_start DateTime64(3),
    window_end   DateTime64(3),
    event        String,
    total        Int64
)
ENGINE = ReplacingMergeTree
ORDER BY (app, window_start, window_end, event);
