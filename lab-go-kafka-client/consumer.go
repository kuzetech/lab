package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

var (
	specifiedTopic                 = "myTopic"
	consumer       *kafka.Consumer = nil
)

func init() {
	c, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
		"group.id":          "myGroup",
		"auto.offset.reset": "earliest",
	})

	if err != nil {
		log.Fatalf("创建 consumer 时失败，原因是：%s \n", err)
	}

	consumer = c

	err = consumer.Subscribe(specifiedTopic, func(c *kafka.Consumer, event kafka.Event) error {
		log.Printf("接收到重平衡事件，内容为：%s \n", event)
		return nil
	})

	if err != nil {
		log.Fatalf("consumer 订阅 topic 时失败，原因是：%s \n", err)
	}
}
