package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
	"time"
)

func main() {
	topic := "myTopic7"
	first := false

	if first {
		log.Printf("产生数据 \n")
		producer := createProduce()

		for _, word := range []string{"Welcome", "to", "the", "Confluent", "Kafka", "Golang", "client"} {
			producer.Produce(&kafka.Message{
				TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
				Value:          []byte(word),
			}, nil)
		}

		producer.Flush(1000)

		producer.Close()

		time.Sleep(time.Second * 3)
	}

	log.Printf("接收数据 \n")

	consumer := createConsumer(topic)

	message, _ := consumer.ReadMessage(time.Second)

	if message != nil {
		consumer.StoreMessage(message)
		log.Println(string(message.Value))
		log.Println(message.TopicPartition.Offset.String())
	}

	consumer.Close()

	log.Println("程序结束")
}
