-- 测试库 ------------------------------------
CREATE TABLE IF NOT EXISTS example_items (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO example_items (name)
VALUES ('hello mysql');
-- --------------------------------------------------------------------


-- hive 元数据库 ----------------------------------
CREATE DATABASE IF NOT EXISTS hive_metastore
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'hive'@'%' IDENTIFIED BY 'hive_password';
ALTER USER 'hive'@'%' IDENTIFIED BY 'hive_password';

GRANT ALL PRIVILEGES ON hive_metastore.* TO 'hive'@'%';
-- --------------------------------------------------------------------


-- 调度数据库 ----------------------------------
CREATE DATABASE IF NOT EXISTS dolphinscheduler
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'dolphinscheduler'@'%' IDENTIFIED BY 'dolphinscheduler_password';
ALTER USER 'dolphinscheduler'@'%' IDENTIFIED BY 'dolphinscheduler_password';

GRANT ALL PRIVILEGES ON dolphinscheduler.* TO 'dolphinscheduler'@'%';
-- --------------------------------------------------------------------


-- 制造业 ERP 数据库 ----------------------------------
CREATE DATABASE IF NOT EXISTS erp
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS erp.user_info (
  id BIGINT NOT NULL AUTO_INCREMENT,
  login_name VARCHAR(200),
  nick_name VARCHAR(200),
  user_level VARCHAR(200),
  birthday DATE,
  gender VARCHAR(1),
  create_time TIMESTAMP NULL DEFAULT NULL,
  operate_time TIMESTAMP NULL DEFAULT NULL,
  status VARCHAR(200),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO erp.user_info
  (id, login_name, nick_name, user_level, birthday, gender, create_time, operate_time, status)
VALUES
  (1, 'zhangsan', '张三', '1', '1998-03-12', 'M', '2024-01-05 09:12:31', '2024-06-11 14:20:00', 'active'),
  (2, 'lisi', '李四', '2', '1995-07-24', 'F', '2024-01-18 10:35:12', '2024-07-02 08:45:22', 'active'),
  (3, 'wangwu', '王五', '1', '2000-11-08', 'M', '2024-02-03 16:22:09', NULL, 'active'),
  (4, 'zhaoliu', '赵六', '3', '1992-05-30', 'F', '2024-02-16 13:05:44', '2024-08-19 19:30:10', 'active'),
  (5, 'sunqi', '孙七', '2', '1989-09-17', 'M', '2024-03-01 08:18:27', '2024-05-21 11:11:11', 'inactive'),
  (6, 'zhouba', '周八', '4', '1997-12-02', 'F', '2024-03-14 20:42:56', NULL, 'active'),
  (7, 'wujie', '吴洁', '1', '2001-04-19', 'F', '2024-04-07 07:55:03', '2024-09-01 12:00:00', 'active'),
  (8, 'zhenghao', '郑浩', '3', '1993-08-26', 'M', '2024-04-22 15:31:48', '2024-07-29 17:16:39', 'locked'),
  (9, 'fengyan', '冯妍', '2', '1999-01-10', 'F', '2024-05-09 12:09:15', NULL, 'active'),
  (10, 'chenfei', '陈飞', '5', '1987-06-06', 'M', '2024-05-27 18:27:33', '2024-08-08 09:09:09', 'active');

CREATE TABLE IF NOT EXISTS erp.sku_info (
  id BIGINT NOT NULL AUTO_INCREMENT,
  spu_id BIGINT,
  price DECIMAL(10, 2),
  sku_name VARCHAR(200),
  sku_desc VARCHAR(2000),
  weight DECIMAL(10, 2),
  tm_id BIGINT,
  tm_name VARCHAR(100),
  category3_id BIGINT,
  category3_name VARCHAR(50),
  is_sale TINYINT,
  create_time TIMESTAMP NULL DEFAULT NULL,
  operate_time TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO erp.sku_info
  (id, spu_id, price, sku_name, sku_desc, weight, tm_id, tm_name, category3_id, category3_name, is_sale, create_time, operate_time)
VALUES
  (1, 1001, 3999.00, 'XPhone 15 128GB 星夜黑', '6.1英寸旗舰手机，128GB存储，星夜黑配色', 0.18, 1, '星河科技', 101, '智能手机', 1, '2024-01-06 09:10:00', '2024-06-01 10:20:00'),
  (2, 1001, 4499.00, 'XPhone 15 256GB 星夜黑', '6.1英寸旗舰手机，256GB存储，星夜黑配色', 0.18, 1, '星河科技', 101, '智能手机', 1, '2024-01-06 09:15:00', '2024-06-01 10:25:00'),
  (3, 1002, 6999.00, 'BookAir 14 16G 512G', '14英寸轻薄办公本，16G内存，512G固态硬盘', 1.28, 2, '云岭电脑', 102, '笔记本电脑', 1, '2024-01-18 14:30:00', NULL),
  (4, 1003, 2599.00, 'Pad Pro 11 WiFi版', '11英寸高刷平板电脑，适合学习与影音娱乐', 0.47, 3, '青木数码', 103, '平板电脑', 1, '2024-02-03 11:22:00', '2024-07-12 16:40:00'),
  (5, 1004, 799.00, 'SoundBuds Pro 降噪耳机', '真无线主动降噪耳机，支持通透模式', 0.06, 4, '声动', 104, '蓝牙耳机', 1, '2024-02-20 08:45:00', NULL),
  (6, 1005, 3299.00, 'VisionCam 4K 运动相机', '4K超清防抖运动相机，适合户外拍摄', 0.15, 5, '视界影像', 105, '运动相机', 1, '2024-03-05 13:18:00', '2024-08-02 09:00:00'),
  (7, 1006, 1299.00, 'HomeBot Mini 扫地机器人', '智能路径规划扫地机器人，支持手机控制', 2.80, 6, '智家', 106, '清洁电器', 1, '2024-03-21 17:05:00', NULL),
  (8, 1007, 199.00, 'FastCharge 100W 充电器', '多协议快充充电器，双口输出', 0.12, 7, '电力方舟', 107, '充电器', 1, '2024-04-02 10:10:00', '2024-05-16 15:30:00'),
  (9, 1008, 599.00, 'FitBand 8 智能手环', '健康监测智能手环，支持多运动模式', 0.04, 8, '跃动科技', 108, '智能手环', 0, '2024-04-19 12:00:00', '2024-09-01 08:30:00'),
  (10, 1009, 1599.00, 'ViewScreen 27 2K 显示器', '27英寸2K办公显示器，低蓝光护眼', 4.60, 9, '明视', 109, '显示器', 1, '2024-05-08 19:25:00', NULL);

CREATE TABLE IF NOT EXISTS erp.order_info (
  id BIGINT NOT NULL AUTO_INCREMENT,
  consignee VARCHAR(100),
  consignee_tel VARCHAR(20),
  total_amount DECIMAL(10, 2),
  order_status VARCHAR(20),
  user_id BIGINT,
  payment_way VARCHAR(20),
  delivery_address VARCHAR(1000),
  order_comment VARCHAR(200),
  out_trade_no VARCHAR(50),
  trade_body VARCHAR(200),
  create_time TIMESTAMP NULL DEFAULT NULL,
  operate_time TIMESTAMP NULL DEFAULT NULL,
  expire_time TIMESTAMP NULL DEFAULT NULL,
  process_status VARCHAR(20),
  tracking_no VARCHAR(100),
  parent_order_id BIGINT,
  img_url VARCHAR(200),
  province_id INT,
  activity_reduce_amount DECIMAL(16, 2),
  coupon_reduce_amount DECIMAL(16, 2),
  original_total_amount DECIMAL(16, 2),
  feight_fee DECIMAL(16, 2),
  feight_fee_reduce DECIMAL(16, 2),
  refundable_time TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO erp.order_info
  (id, consignee, consignee_tel, total_amount, order_status, user_id, payment_way, delivery_address, order_comment, out_trade_no, trade_body, create_time, operate_time, expire_time, process_status, tracking_no, parent_order_id, img_url, province_id, activity_reduce_amount, coupon_reduce_amount, original_total_amount, feight_fee, feight_fee_reduce, refundable_time)
VALUES
  (1, '张三', '13800010001', 3999.00, '1001', 1, 'alipay', '北京市朝阳区望京街道科技园1号', '请工作日配送', 'OT202405010001', 'XPhone 15 128GB 星夜黑', '2024-05-01 09:15:12', '2024-05-01 09:20:30', '2024-05-01 09:45:12', 'paid', 'SF100000001', NULL, 'https://img.example.com/order/1.jpg', 11, 200.00, 100.00, 4299.00, 12.00, 12.00, '2024-05-08 09:15:12'),
  (2, '李四', '13800010002', 4499.00, '1001', 2, 'wechat', '上海市浦东新区张江高科技园区88号', NULL, 'OT202405010002', 'XPhone 15 256GB 星夜黑', '2024-05-01 10:05:44', '2024-05-01 10:12:18', '2024-05-01 10:35:44', 'paid', 'YD100000002', NULL, 'https://img.example.com/order/2.jpg', 31, 150.00, 50.00, 4699.00, 10.00, 10.00, '2024-05-08 10:05:44'),
  (3, '王五', '13800010003', 6999.00, '1002', 3, 'bank_card', '广东省深圳市南山区软件产业基地A座', '发票抬头个人', 'OT202405020001', 'BookAir 14 16G 512G', '2024-05-02 14:22:09', '2024-05-02 15:01:00', '2024-05-02 14:52:09', 'shipped', 'JD100000003', NULL, 'https://img.example.com/order/3.jpg', 44, 300.00, 0.00, 7299.00, 20.00, 20.00, '2024-05-09 14:22:09'),
  (4, '赵六', '13800010004', 2599.00, '1002', 4, 'alipay', '浙江省杭州市西湖区文三路199号', NULL, 'OT202405020002', 'Pad Pro 11 WiFi版', '2024-05-02 16:40:35', '2024-05-02 17:05:10', '2024-05-02 17:10:35', 'shipped', 'ZTO100000004', NULL, 'https://img.example.com/order/4.jpg', 33, 100.00, 0.00, 2699.00, 8.00, 8.00, '2024-05-09 16:40:35'),
  (5, '孙七', '13800010005', 799.00, '1003', 5, 'wechat', '江苏省南京市鼓楼区中央路66号', '放门卫即可', 'OT202405030001', 'SoundBuds Pro 降噪耳机', '2024-05-03 08:18:27', '2024-05-03 12:30:00', '2024-05-03 08:48:27', 'completed', 'STO100000005', NULL, 'https://img.example.com/order/5.jpg', 32, 40.00, 20.00, 859.00, 6.00, 6.00, '2024-05-10 08:18:27'),
  (6, '周八', '13800010006', 3299.00, '1001', 6, 'alipay', '四川省成都市高新区天府大道中段500号', NULL, 'OT202405030002', 'VisionCam 4K 运动相机', '2024-05-03 11:42:56', '2024-05-03 11:55:42', '2024-05-03 12:12:56', 'paid', 'SF100000006', NULL, 'https://img.example.com/order/6.jpg', 51, 120.00, 80.00, 3499.00, 15.00, 15.00, '2024-05-10 11:42:56'),
  (7, '吴洁', '13800010007', 1299.00, '1004', 7, 'wechat', '湖北省武汉市洪山区珞喻路1037号', '周末也可配送', 'OT202405040001', 'HomeBot Mini 扫地机器人', '2024-05-04 13:05:03', '2024-05-04 13:35:18', '2024-05-04 13:35:03', 'cancelled', NULL, NULL, 'https://img.example.com/order/7.jpg', 42, 60.00, 40.00, 1399.00, 10.00, 10.00, NULL),
  (8, '郑浩', '13800010008', 199.00, '1003', 8, 'alipay', '陕西省西安市雁塔区科技路18号', NULL, 'OT202405040002', 'FastCharge 100W 充电器', '2024-05-04 19:30:48', '2024-05-05 09:16:39', '2024-05-04 20:00:48', 'completed', 'YTO100000008', NULL, 'https://img.example.com/order/8.jpg', 61, 0.00, 10.00, 209.00, 5.00, 5.00, '2024-05-11 19:30:48'),
  (9, '冯妍', '13800010009', 599.00, '1002', 9, 'bank_card', '福建省厦门市思明区软件园二期12号', '尽快发货', 'OT202405050001', 'FitBand 8 智能手环', '2024-05-05 12:09:15', '2024-05-05 12:25:00', '2024-05-05 12:39:15', 'shipped', 'EMS100000009', NULL, 'https://img.example.com/order/9.jpg', 35, 20.00, 30.00, 649.00, 6.00, 6.00, '2024-05-12 12:09:15'),
  (10, '陈飞', '13800010010', 1599.00, '1001', 10, 'wechat', '重庆市渝中区解放碑步行街9号', NULL, 'OT202405050002', 'ViewScreen 27 2K 显示器', '2024-05-05 18:27:33', '2024-05-05 18:40:09', '2024-05-05 18:57:33', 'paid', 'DB100000010', NULL, 'https://img.example.com/order/10.jpg', 50, 80.00, 20.00, 1699.00, 25.00, 25.00, '2024-05-12 18:27:33');
-- --------------------------------------------------------------------


-- 重新加载权限 ----------------------------------
FLUSH PRIVILEGES;
-- --------------------------------------------------------------------
