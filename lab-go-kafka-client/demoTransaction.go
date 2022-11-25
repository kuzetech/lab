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

func testTransaction() {
	consumerTopic := "consumerTopic"
	outputTopic := "outputTopic"

	var wg sync.WaitGroup

	stopCtx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	consumer := createTransactionConsumer(consumerTopic)
	defer consumer.Close()
	producer := createTransactionProduce()
	defer producer.Close()

	maxDuration, _ := time.ParseDuration("2s")
	ctx, cancel := context.WithTimeout(context.Background(), maxDuration)
	defer cancel()

	start := time.Now()
	err := producer.InitTransactions(ctx)
	duration := time.Now().Sub(start).Seconds()

	if err != nil {
		consumer.Close()
		producer.Close()
		if err.(kafka.Error).Code() == kafka.ErrTimedOut {
			log.Fatalf("producer InitTransactions ErrTimedOut, err: %v", err)
		} else {
			log.Fatalf("producer InitTransactions err: %v", err)
		}
	}

	if duration < maxDuration.Seconds()*0.8 || duration > maxDuration.Seconds()*1.2 {
		log.Printf("InitTransactions() should have finished within %.2f +-20%%, not %.2f", maxDuration.Seconds(), duration)
	}

	wg.Add(1)
	go func() {
		for {
			select {
			case sig := <-stopCtx.Done():
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
				}
			}
		}
	}()

	wg.Add(1)
	go func() {
		for {
			select {
			case sig := <-stopCtx.Done():
				log.Printf("Consumer Caught signal %v: terminating\n", sig)
				wg.Done()
				return
			default:
				event := consumer.Poll(100)

				if event == nil {
					continue
				}

				switch e := event.(type) {
				case *kafka.Message:
					valueStr := string(e.Value)
					log.Printf("接收到消息，Message on %s: %s \n", e.TopicPartition, valueStr)
					if e.Headers != nil {
						log.Printf("Headers: %v\n", e.Headers)
					}

					err = producer.BeginTransaction()
					if err != nil {
						log.Fatalf("开启事务时出现错误，原因是：%v \n", err)
					}
					producer.Produce(&kafka.Message{
						TopicPartition: kafka.TopicPartition{Topic: &outputTopic, Partition: kafka.PartitionAny},
						Value:          []byte(valueStr + "=-="),
					}, nil)

					commitDuration, _ := time.ParseDuration("2s")
					commitTimeoutCtx, commitTimeoutCancel := context.WithTimeout(context.Background(), commitDuration)

					offsets := []kafka.TopicPartition{e.TopicPartition}
					offsets[0].Offset++
					metadata, getConsumerGroupMetadataErr := consumer.GetConsumerGroupMetadata()
					if getConsumerGroupMetadataErr != nil {
						commitTimeoutCancel()
						log.Fatalf("获取 ConsumerGroupMetadata 时出现错误，原因是：%v \n", getConsumerGroupMetadataErr)
					}
					err = producer.SendOffsetsToTransaction(commitTimeoutCtx, offsets, metadata)
					if err != nil {
						commitTimeoutCancel()
						log.Fatalf("SendOffsetsToTransaction 时出现错误，原因是：%v \n", err)
					}

					err = producer.CommitTransaction(commitTimeoutCtx)
					if err != nil {
						commitTimeoutCancel()
						log.Fatalf("提交事务时出现错误，原因是：%v \n", err)
					}

					commitTimeoutCancel()

				case kafka.Error:
					// Errors should generally be considered informational, the client will try to automatically recover.
					if e.Code() == kafka.ErrAllBrokersDown {

					}
					log.Fatalf("接收到错误，Error: %v: %v\n", e.Code(), e)
				case kafka.OffsetsCommitted:
					// 使用这种方式不会收到该事件
					log.Printf("接收到偏移量提交消息，OffsetsCommitted: %v \n", e)
				default:
					log.Printf("Ignored %v\n", e)
				}
			}
		}
	}()

	wg.Wait()

	log.Println("程序结束")
}
