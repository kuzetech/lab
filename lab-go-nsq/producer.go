package main

import (
	"github.com/nsqio/go-nsq"
	"log"
)

func createProducer() *nsq.Producer {
	config := nsq.NewConfig()
	// 连接到 nsqd
	producer, err := nsq.NewProducer("127.0.0.1:4150", config)
	if err != nil {
		log.Fatal(err)
	}
	return producer
}
