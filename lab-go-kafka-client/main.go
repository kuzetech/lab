package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

func main() {

	for _, word := range []string{"Welcome", "to", "the", "Confluent", "Kafka", "Golang", "client"} {
		producer.Produce(&kafka.Message{
			TopicPartition: kafka.TopicPartition{Topic: &specifiedTopic, Partition: kafka.PartitionAny},
			Value:          []byte(word),
		}, nil)
	}
	producer.Flush(3000)
	defer producer.Close()

	for {
		msg, err := consumer.ReadMessage(-1)
		if err == nil {
			log.Printf("消费成功 %s: %s\n", msg.TopicPartition, string(msg.Value))
		} else {
			// The client will automatically try to recover from all errors.
			log.Printf("消费失败: %v (%v)\n", err, msg)
		}
	}

	defer consumer.Close()

}
