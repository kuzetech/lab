CREATE DATABASE IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS app.record
(
    window_start DateTime64(3),
    window_end   DateTime64(3),
    event        String,
    total        Int64
)
ENGINE = ReplacingMergeTree
ORDER BY (window_start, window_end, event);

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
