-- ----------------------------
-- Table structure for record
-- ----------------------------
DROP TABLE IF EXISTS record;
CREATE TABLE record
(
    window_start timestamp(3) not null,
    window_end   timestamp(3) not null,
    event        varchar(255) not null,
    total        bigint       not null,
    constraint record_pk
    primary key (window_start, window_end, event)
);


DROP TABLE IF EXISTS counter;
CREATE TABLE counter
(
    app          varchar(255) not null,
    window_start timestamp(3) not null,
    window_end   timestamp(3) not null,
    event        varchar(255) not null,
    total        bigint       not null,
    constraint counter_pk
        primary key (app, window_start, window_end, event)
);