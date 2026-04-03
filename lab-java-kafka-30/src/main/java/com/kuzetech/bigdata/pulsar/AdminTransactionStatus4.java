package com.kuzetech.bigdata.pulsar;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTransactionsResult;
import org.apache.kafka.clients.admin.TransactionDescription;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class AdminTransactionStatus4 {
    public static void main(String[] args) throws Exception {
        Properties adminProps = new Properties();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        Admin admin = Admin.create(adminProps);

        final String transactionalId = "my-transactional-id-002";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        producer.initTransactions();
        producer.beginTransaction();
        producer.send(new ProducerRecord<>("test", "test"));
        producer.flush();

        Object txnManager = getField(producer, "transactionManager");
        Object pidAndEpoch = getField(txnManager, "producerIdAndEpoch");
        long pid = (long) getField(pidAndEpoch, "producerId");
        short epoch = (short) getField(pidAndEpoch, "epoch");
        Set<TopicPartition> topicPartitions = (Set<TopicPartition>) getField(txnManager, "partitionsInTransaction");

        System.out.println("Wait! Storing PID: " + pid + ", Epoch: " + epoch + ", TopicPartitions: " + topicPartitions);

        producer.close(Duration.ZERO);

        DescribeTransactionsResult result = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description = result.description(transactionalId).get();
        System.out.println("---------------------------------");
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description.state());
        System.out.println("关联的 Producer ID: " + description.producerId());
        System.out.println("事务超时时间: " + description.transactionTimeoutMs() + "ms");

        //创建一个新的生产者
        KafkaProducer<String, String> newProducer = new KafkaProducer<>(props);
        Object newProducerTxnManager = getField(newProducer, "transactionManager");

        //注入 pid 和 epoch
        Class<?> pidAndEpochClass = Class.forName("org.apache.kafka.common.utils.ProducerIdAndEpoch");
        Constructor<?> constructor = pidAndEpochClass.getDeclaredConstructor(long.class, short.class);
        constructor.setAccessible(true);
        Object newPidAndEpoch = constructor.newInstance(pid, epoch);
        setField(newProducerTxnManager, "producerIdAndEpoch", newPidAndEpoch);

        //强制修改状态机
        //Kafka 内部检查 commitTransaction 必须在特定的状态下，我们需要将其改为已初始化或进行中
        Class<?> stateClass = Class.forName("org.apache.kafka.clients.producer.internals.TransactionManager$State");
        Object readyState = null;
        for (Object obj : stateClass.getEnumConstants()) {
            if (obj.toString().equals("IN_TRANSACTION")) { // 或者使用 IN_TRANSACTION，视具体版本而定
                readyState = obj;
                break;
            }
        }
        setField(newProducerTxnManager, "currentState", readyState);

        // 3. 手动添加你之前发送过数据的分区
        // 这一步至关重要：没有它，EndTxnRequest 永远发不出去
        Field tpField = newProducerTxnManager.getClass().getDeclaredField("partitionsInTransaction");
        tpField.setAccessible(true);
        Set<TopicPartition> newTopicPartitions = (Set<TopicPartition>) tpField.get(newProducerTxnManager);
        newTopicPartitions.addAll(topicPartitions);

        newProducer.commitTransaction();

        Thread.sleep(3000);
        DescribeTransactionsResult result2 = admin.describeTransactions(Collections.singletonList(transactionalId));
        TransactionDescription description2 = result2.description(transactionalId).get();
        System.out.println("---------------------------------");
        System.out.println("事务 ID: " + transactionalId);
        System.out.println("当前状态: " + description2.state());
        System.out.println("关联的 Producer ID: " + description2.producerId());
        System.out.println("事务超时时间: " + description2.transactionTimeoutMs() + "ms");


        admin.close();
        newProducer.close();
    }

    private static Object getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
