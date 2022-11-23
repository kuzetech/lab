package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

func createProduce() *kafka.Producer {
	p, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
	})

	if err != nil {
		log.Fatalf("创建 producer 时失败，原因是：%s \n", err)
	}

	return p
}

func createTransactionProduce() *kafka.Producer {
	p, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers":      "localhost:9092",
		"transactional.id":       "test", // 设置 transactional.id 后 enable.idempotence 也会被设置成 true 开启幂等
		"transaction.timeout.ms": "4000", // 开始一个事务到结束一个事务的最长事件
	})

	if err != nil {
		log.Fatalf("创建 producer 时失败，原因是：%s \n", err)
	}

	return p
}
