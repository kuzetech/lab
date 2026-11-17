CREATE SCHEMA "验证模式";
CREATE TABLE "验证模式"."订单🚀" (
    "识别码" INTEGER PRIMARY KEY,
    "客户名称" VARCHAR(100) NOT NULL,
    "状态_🚀" VARCHAR(100)
);
ALTER TABLE "验证模式"."订单🚀" REPLICA IDENTITY FULL;
CREATE PUBLICATION cdc_utf8_publication FOR TABLE "验证模式"."订单🚀";
INSERT INTO "验证模式"."订单🚀" ("识别码", "客户名称", "状态_🚀") VALUES
    (1, '快照-初始-A', '正常 ✅'),
    (2, '快照-初始-B', '待处理 ⏳');
