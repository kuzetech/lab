package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

func createAdmin() *kafka.AdminClient {
	a, err := kafka.NewAdminClient(&kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
	})

	if err != nil {
		log.Fatalf("创建 AdminClient 时失败，原因是：%s \n", err)
	}

	return a
}
