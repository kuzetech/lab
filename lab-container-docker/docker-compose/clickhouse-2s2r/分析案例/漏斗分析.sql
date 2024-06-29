-- 初始化表结构
create table test_funnel (
    uid         String      COMMENT '用户',
    eventId     String      COMMENT '事件名称',
    city        String      COMMENT '事件发生所在城市',
    region      String      COMMENT '大区名称',
    eventTime   UInt64      COMMENT '事件时间'
) engine = Memory;


-- 写入测试数据
insert into test_funnel values 
('小明', '登录', '厦门', '服务器1', 20200101), ('小明', '升级', '厦门', '服务器1', 20200102), ('小明', '充值', '厦门', '服务器1', 20200103),
('小东', '登录', '厦门', '服务器2', 20200101), ('小东', '升级', '厦门', '服务器2', 20200102), ('小东', '充值', '厦门', '服务器2', 20200103),
('小红', '登录', '厦门', '服务器1', 20200101), ('小红', '升级', '厦门', '服务器1', 20200102), ('小红', '升级', '厦门', '服务器1', 20200103), ('小红', '充值', '厦门', '服务器1', 20200103),
('小张', '登录', '厦门', '服务器1', 20200101), ('小张', '升级', '厦门', '服务器1', 20200103), ('小张', '充值', '厦门', '服务器1', 20200103),
('小黄', '登录', '北京', '服务器1', 20200101), ('小黄', '升级', '北京', '服务器1', 20200103), ('小黄', '充值', '北京', '服务器1', 20200103),
('小黑', '登录', '北京', '服务器1', 20200101), ('小黑', '升级', '北京', '服务器1', 20200103),
('小绿', '登录', '厦门', '服务器1', 20200101);


-- 执行语句
SELECT 
    city,
    level_index,
    groupArray(uid) as uids,
    count(1) as current,
    neighbor(current, 1) as last,
    if(last = 0, 1, round((current/last), 2)) as keep_rate,  -- 留存率，保留两位小数 
    if(last = 0, 0, round(((last-current)/last), 2)) as loss_rate  -- 流失率，保留两位小数 
FROM (
    SELECT 
        city,
        uid, 
        arrayWithConstant(level, 1) as levels,  -- 生成一个 item 都是 1 的指定长度的数组 
        arrayJoin(arrayEnumerate(levels)) as level_index  -- arrayEnumerate 返回数组每个元素的下标
    FROM (
        SELECT 
            city, 
            uid, 
            has(groupUniqArray(eventId), '登录') as exist_begin_event,  -- 返回 0 或 1 
            (windowFunnel(1)(eventTime, eventId= '登录', eventId= '升级', eventId= '充值') + 1) AS level  -- 分析窗口期为1天, +1 是为了兼容开始事件
        FROM (
            SELECT * 
            FROM (
                SELECT * 
                FROM test_funnel 
                WHERE (eventId = '登录' and city != '海南')  -- 该行是单独的事件过滤 
                or  eventId = '升级' 
                or  eventId = '充值'
            ) 
            WHERE region = '服务器1'  -- 该行是全局筛选条件 
            and eventTime between 20200101 and 20200103  -- 该行是 事件时间筛选区间 
        ) 
        GROUP BY city, uid  -- 增加分组项 
        having exist_begin_event != 0  -- 排除没有开始事件的用户 
    ) 
) 
group by city, level_index  -- 增加分组项
ORDER BY city asc,level_index asc