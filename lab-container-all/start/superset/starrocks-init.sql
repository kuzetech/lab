-- This script is executed by the starrocks-init Compose service.
-- It models dws_trade_province_order_1d with 1,000 deterministic rows.
CREATE DATABASE IF NOT EXISTS gesac_trade;
USE gesac_trade;

CREATE TABLE IF NOT EXISTS dws_trade_province_order_1d (
  dt VARCHAR(20) NOT NULL,
  time_point VARCHAR(20) NOT NULL,
  time_interval VARCHAR(20) NOT NULL,
  province_id BIGINT NOT NULL,
  province_name VARCHAR(32) NOT NULL,
  area_code VARCHAR(20) NOT NULL,
  iso_code VARCHAR(20) NOT NULL,
  iso_3166_2 VARCHAR(20) NOT NULL,
  order_original_amount_1d DECIMAL(16,2) NOT NULL,
  activity_reduce_amount_1d DECIMAL(16,2) NOT NULL,
  coupon_reduce_amount_1d DECIMAL(16,2) NOT NULL,
  order_total_amount_1d DECIMAL(16,2) NOT NULL
) ENGINE=OLAP
PRIMARY KEY(dt, time_point, time_interval, province_id, province_name, area_code, iso_code, iso_3166_2)
DISTRIBUTED BY HASH(province_id) BUCKETS 4
PROPERTIES ('replication_num' = '1');

INSERT INTO dws_trade_province_order_1d
WITH source_numbers AS (
  SELECT generate_series AS n FROM TABLE(generate_series(1, 1000))
), province_dim AS (
  SELECT 1 AS province_id, 'Beijing' AS province_name, '110000' AS area_code, 'CN' AS iso_code, 'CN-BJ' AS iso_3166_2
  UNION ALL SELECT 2, 'Tianjin', '120000', 'CN', 'CN-TJ'
  UNION ALL SELECT 3, 'Hebei', '130000', 'CN', 'CN-HE'
  UNION ALL SELECT 4, 'Shanxi', '140000', 'CN', 'CN-SX'
  UNION ALL SELECT 5, 'Inner Mongolia', '150000', 'CN', 'CN-NM'
  UNION ALL SELECT 6, 'Liaoning', '210000', 'CN', 'CN-LN'
  UNION ALL SELECT 7, 'Jilin', '220000', 'CN', 'CN-JL'
  UNION ALL SELECT 8, 'Heilongjiang', '230000', 'CN', 'CN-HL'
  UNION ALL SELECT 9, 'Shanghai', '310000', 'CN', 'CN-SH'
  UNION ALL SELECT 10, 'Jiangsu', '320000', 'CN', 'CN-JS'
  UNION ALL SELECT 11, 'Zhejiang', '330000', 'CN', 'CN-ZJ'
  UNION ALL SELECT 12, 'Anhui', '340000', 'CN', 'CN-AH'
  UNION ALL SELECT 13, 'Fujian', '350000', 'CN', 'CN-FJ'
  UNION ALL SELECT 14, 'Jiangxi', '360000', 'CN', 'CN-JX'
  UNION ALL SELECT 15, 'Shandong', '370000', 'CN', 'CN-SD'
  UNION ALL SELECT 16, 'Henan', '410000', 'CN', 'CN-HA'
  UNION ALL SELECT 17, 'Hubei', '420000', 'CN', 'CN-HB'
  UNION ALL SELECT 18, 'Hunan', '430000', 'CN', 'CN-HN'
  UNION ALL SELECT 19, 'Guangdong', '440000', 'CN', 'CN-GD'
  UNION ALL SELECT 20, 'Guangxi', '450000', 'CN', 'CN-GX'
  UNION ALL SELECT 21, 'Hainan', '460000', 'CN', 'CN-HI'
  UNION ALL SELECT 22, 'Chongqing', '500000', 'CN', 'CN-CQ'
  UNION ALL SELECT 23, 'Sichuan', '510000', 'CN', 'CN-SC'
  UNION ALL SELECT 24, 'Guizhou', '520000', 'CN', 'CN-GZ'
  UNION ALL SELECT 25, 'Yunnan', '530000', 'CN', 'CN-YN'
  UNION ALL SELECT 26, 'Tibet', '540000', 'CN', 'CN-XZ'
  UNION ALL SELECT 27, 'Shaanxi', '610000', 'CN', 'CN-SN'
  UNION ALL SELECT 28, 'Gansu', '620000', 'CN', 'CN-GS'
  UNION ALL SELECT 29, 'Qinghai', '630000', 'CN', 'CN-QH'
  UNION ALL SELECT 30, 'Ningxia', '640000', 'CN', 'CN-NX'
  UNION ALL SELECT 31, 'Xinjiang', '650000', 'CN', 'CN-XJ'
  UNION ALL SELECT 32, 'Taiwan', '710000', 'CN', 'CN-TW'
  UNION ALL SELECT 33, 'Hong Kong', '810000', 'CN', 'CN-HK'
  UNION ALL SELECT 34, 'Macau', '820000', 'CN', 'CN-MO'
), amounts AS (
  SELECT n,
    CAST(100 + MOD(n * 37, 9000) / 10.0 AS DECIMAL(16,2)) AS original_amount,
    CAST(MOD(n * 13, 300) / 10.0 AS DECIMAL(16,2)) AS activity_amount,
    CAST(MOD(n * 17, 200) / 10.0 AS DECIMAL(16,2)) AS coupon_amount
  FROM source_numbers
)
SELECT
  CONCAT('2026-09-', LPAD(CAST(MOD(n - 1, 28) + 1 AS VARCHAR), 2, '0')),
  CONCAT('2026-09-', LPAD(CAST(MOD(n - 1, 28) + 1 AS VARCHAR), 2, '0'), ' ', LPAD(CAST(MOD(n * 7, 24) AS VARCHAR), 2, '0'), ':', LPAD(CAST(MOD(n * 13, 60) AS VARCHAR), 2, '0')),
  '1m', province_dim.province_id, province_dim.province_name,
  province_dim.area_code, province_dim.iso_code, province_dim.iso_3166_2,
  original_amount, activity_amount, coupon_amount,
  CAST(original_amount - activity_amount - coupon_amount AS DECIMAL(16,2))
FROM amounts
JOIN province_dim ON province_dim.province_id = MOD(amounts.n - 1, 34) + 1;
