package main

import (
	"github.com/nsqio/go-nsq"
	"log"
)

var UseTopic = "test"
var UseChannel = "hsw"

func createConsumer() *nsq.Consumer {
	// Instantiate a consumer that will subscribe to the provided channel.
	config := nsq.NewConfig()
	consumer, err := nsq.NewConsumer(UseTopic, UseChannel, config)
	if err != nil {
		log.Fatal(err)
	}

	// Set the Handler for messages received by this Consumer. Can be called multiple times.
	// See also AddConcurrentHandlers.
	consumer.AddHandler(&myMessageHandler{})

	// Use nsqlookupd to discover nsqd instances.
	// See also ConnectToNSQD, ConnectToNSQDs, ConnectToNSQLookupds.
	// 本来应该连接到 nsqlookupd ，但由于是 docker-compose 启动，转发后的地址连接不上 nsqd
	err = consumer.ConnectToNSQDs([]string{"127.0.0.1:4150"})
	if err != nil {
		log.Fatal(err)
	}
	return consumer
}

type myMessageHandler struct{}

func (h *myMessageHandler) HandleMessage(m *nsq.Message) error {
	if len(m.Body) == 0 {
		// Returning nil will automatically send a FIN command to NSQ to mark the message as processed.
		// In this case, a message with an empty body is simply ignored/discarded.
		return nil
	}

	// do whatever actual message processing is desired
	log.Printf("接收到消息，内容为：%s", string(m.Body))

	// Returning a non-nil error will automatically send a REQ command to NSQ to re-queue the message.
	return nil
}
