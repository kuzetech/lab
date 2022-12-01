package main

import (
	"context"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/google/uuid"
	"log"
	"os/signal"
	"syscall"
	"time"
)

func testTransactionRestart() {

	uuid, _ := uuid.NewUUID()
	consumerTopic := uuid.String()

	stopCtx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)

	go func() {
		consumer := createTransactionConsumer(consumerTopic)
		defer consumer.Close()
		for {
			select {
			case sig := <-stopCtx.Done():
				log.Printf("Consumer Caught signal %v: terminating\n", sig)
				return
			default:
				msg, err := consumer.ReadMessage(-1)

				if err != nil {
					log.Printf("接收消息时发生错误 %v \n", err)
					continue
				}

				log.Printf("接收到消息 %s \n", string(msg.Value))
			}
		}
	}()

	producer := createTransactionProduce()
	err := producer.InitTransactions(context.Background())

	if err != nil {
		log.Fatalf("producer InitTransactions1 err %v \n", err)
	}

	err = producer.BeginTransaction()
	if err != nil {
		log.Fatalf("producer BeginTransaction1 err %v \n", err)
	}

	deliveryChan := make(chan kafka.Event)

	err = producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &consumerTopic, Partition: kafka.PartitionAny},
		Value:          []byte("=-=1"),
	}, deliveryChan)

	e := <-deliveryChan
	log.Printf("接收到发送1消息的回复 %s \n", e.String())

	producer.Close()

	time.Sleep(time.Second * 5)

	producer = createTransactionProduce()
	// this aborts the unfinished transaction of the previous producer
	err = producer.InitTransactions(context.Background())

	if err != nil {
		log.Fatalf("producer InitTransactions2 err %v \n", err)
	}

	err = producer.BeginTransaction()
	if err != nil {
		log.Fatalf("producer BeginTransaction2 err %v \n", err)
	}

	err = producer.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &consumerTopic, Partition: kafka.PartitionAny},
		Value:          []byte("=-=2"),
	}, nil)

	if err != nil {
		log.Fatalf("producer send 2 err %v \n", err)
	}

	log.Printf("准备提交事务 \n")
	err = producer.CommitTransaction(context.Background())

	if err != nil {
		log.Fatalf("producer CommitTransaction err %v \n", err)
	}

	time.Sleep(time.Second * 5)

	producer.Close()
	cancel()

	log.Println("程序结束")
}
