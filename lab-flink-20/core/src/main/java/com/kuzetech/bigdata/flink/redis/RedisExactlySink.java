package com.kuzetech.bigdata.flink.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeutils.base.VoidSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.streaming.api.functions.sink.TwoPhaseCommitSinkFunction;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.lang.reflect.Field;

@Slf4j
public class RedisExactlySink<T> extends TwoPhaseCommitSinkFunction<T, RedisUtil, Void> {
    //定义redis hash表名
    public static final String REDIS_HASH_MAP = "WordAndWordCount";

    public static RedisUtil redisUtil;

    static {
        try {
            redisUtil = new RedisUtil();
        } catch (IOException e) {
            log.error("init RedisUtil error", e);
        }
    }

    //继承父类的构造参数，因为要初始化父类的内容
    public RedisExactlySink() {
        super(new KryoSerializer<>(RedisUtil.class, new ExecutionConfig()), VoidSerializer.INSTANCE);
    }

    @Override
    protected void invoke(RedisUtil transaction, T value, Context context) throws Exception {
        Transaction jedis = transaction.getTransaction();//拿到事务连接
        System.out.println(jedis);
        Class<?> aClass = value.getClass();//获取class
        Field[] fields = aClass.getDeclaredFields();//获取属性字段
        fields[0].setAccessible(true);
        fields[1].setAccessible(true);
        Object object1 = fields[0].get(value);
        if (object1.toString().equals("error")) {
            throw new RuntimeException("主动触发异常！！");
        }
        Object object2 = fields[1].get(value);
        System.out.println("写入redis HashMap ");
        jedis.hset(REDIS_HASH_MAP, object1.toString(), object2.toString());
    }

    @Override
    protected RedisUtil beginTransaction() throws Exception {
        return redisUtil;
    }

    @Override
    protected void preCommit(RedisUtil transaction) throws Exception {
        System.out.println("正在执行预提交!!!");
    }

    @Override
    protected void commit(RedisUtil transaction) {
        Transaction jedistransaction = transaction.getTransaction();
        System.out.println(jedistransaction);
        try {
            System.out.println("事务提交");
            jedistransaction.exec();
            redisUtil.setJedisTransactionIsNull();
        } catch (Exception e) {
            log.error("commit redis Transaction error", e);
        } finally {
            System.out.println("jedis close!!!");
        }
    }

    @Override
    protected void abort(RedisUtil transaction) {
        Transaction jedistransaction = transaction.getTransaction();
        System.out.println(jedistransaction);
        try {
            System.out.println("取消事务");
            jedistransaction.discard();
            redisUtil.setJedisTransactionIsNull();
        } catch (Exception e) {
            log.error("abort redis Transaction error", e);
        } finally {
            System.out.println("jedis close!!!");
        }
    }
}
