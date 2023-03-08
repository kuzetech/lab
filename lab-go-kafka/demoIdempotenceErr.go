package main

import (
	"context"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
	"os/signal"
	"sync"
	"syscall"
	"time"
)

func testIdempotenceErr() {

	produceTopic := "testIdempotence3"

	var wg sync.WaitGroup

	ctx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	producer := createIdempotenceProduce()

	wg.Add(1)
	go func() {
		for {
			select {
			case sig := <-ctx.Done():
				log.Printf("Producer Confirm Caught signal %v: terminating\n", sig)
				wg.Done()
				return
			case e := <-producer.Events():
				switch ev := e.(type) {
				case *kafka.Message:
					if ev.TopicPartition.Error != nil {
						log.Fatalf("数据发送失败，具体的分区信息为: %v ，发送失败的原因为：%s \n", ev.TopicPartition, ev.TopicPartition.Error)
					} else {
						log.Printf("数据发送成功，具体的分区信息为: %v \n", ev.TopicPartition)
					}
				case kafka.Error:
					log.Printf("接收到错误，Error: %v: %v\n", ev.Code(), e)
					if ev.Code() == kafka.ErrOutOfOrderSequenceNumber {
						log.Printf("接收到错误，ErrOutOfOrderSequenceNumber \n")
					} else if ev.IsFatal() {
						log.Printf("接收到错误，IsFatal \n")
					}
				}
			}
		}
	}()

	producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &produceTopic, Partition: kafka.PartitionAny},
		Value:          []byte(time.Now().Format("2006-01-02T15:04:05")),
	}, nil)

	time.Sleep(time.Minute * 30)

	producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &produceTopic, Partition: kafka.PartitionAny},
		Value:          []byte(time.Now().Format("2006-01-02T15:04:05")),
	}, nil)

	wg.Wait()

	log.Println("程序结束")

}
