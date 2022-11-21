package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

var (
	producer *kafka.Producer = nil
)

func init() {
	p, err := kafka.NewProducer(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
	})

	if err != nil {
		log.Fatalf("创建 producer 时失败，原因是：%s \n", err)
	}

	producer = p

	go func() {
		for {
			select {
			case e := <-producer.Events():
				switch ev := e.(type) {
				case *kafka.Message:
					if ev.TopicPartition.Error != nil {
						log.Printf("数据发送失败，具体的分区信息为: %v \n", ev.TopicPartition)
						log.Printf("发送失败的原因为：%s \n", ev.TopicPartition.Error)
					} else {
						log.Printf("数据发送成功，具体的分区信息为: %v \n", ev.TopicPartition)
					}
				}
			}
		}
	}()

}
