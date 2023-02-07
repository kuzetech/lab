package main

import (
	"context"
	"encoding/json"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
	"os/signal"
	"sync"
	"syscall"
	"time"
)

func testNormal() {

	topic := "myTopic"

	var wg sync.WaitGroup

	ctx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	deadline, _ := context.WithDeadline(context.Background(), time.Now().Add(time.Second*10))

	producer := createProduce()

	wg.Add(1)
	go func() {
		for {
			select {
			case sig := <-ctx.Done():
				log.Printf("Producer Confirm Caught signal %v: terminating\n", sig)
				wg.Done()
				return
			case sig := <-deadline.Done():
				log.Printf("Producer Confirm Caught deadline %v: terminating\n", sig)
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

	for _, word := range []string{"Welcome", "to", "the", "Confluent", "Kafka", "Golang", "client"} {
		producer.Produce(&kafka.Message{
			TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
			Value:          []byte(word),
		}, nil)
	}

	unFlushMessageCount := producer.Flush(3000)
	if unFlushMessageCount > 0 {
		log.Fatalf("存在未发送的消息")
	}

	producer.Close()

	time.Sleep(time.Second * 5)
	log.Printf("开始接收时间: %s\n", time.Now().Format(time.RFC3339Nano))

	consumer := createConsumer(topic)

	wg.Add(1)
	go func() {
		for {
			select {
			case sig := <-ctx.Done():
				log.Printf("Consumer Caught signal %v: terminating\n", sig)

				// consumer.Commit()                   // 将 offset store 中的暂存的偏移量提交，如果 store 中不存在偏移量会报错，成功提交后清除 store 内容
				// consumer.CommitMessage(m * Message) // 仅根据 message 提交该分区的偏移量，成功提交后清除 store 内容
				// consumer.CommitOffsets(offsets []TopicPartition)  // 根据给定的 offsets 提交偏移量，成功提交后清除 store 内容

				// consumer.Committed(partitions []TopicPartition, timeoutMs int) // 查询给定 partitions 提交到服务器的偏移量

				_, err := consumer.Commit()
				if err != nil {
					log.Fatalf("手动提交 offset 失败，原因是：%v \n", err)
				} else {
					log.Printf("手动提交 offset 成功 \n")
				}
				err = consumer.Close()
				if err != nil {
					log.Fatalf("关闭 consumer 时出现错误，原因是：%v \n", err)
				}
				wg.Done()
				return
			case sig := <-deadline.Done():
				log.Printf("Consumer Caught deadline %v: terminating\n", sig)
				_, err := consumer.Commit()
				if err != nil {
					log.Fatalf("手动提交 offset 失败，原因是：%v \n", err)
				} else {
					log.Printf("手动提交 offset 成功 \n")
				}
				err = consumer.Close()
				if err != nil {
					log.Fatalf("关闭 consumer 时出现错误，原因是：%v \n", err)
				}
				wg.Done()
				return
			default:
				// consumer.ReadMessage() 是对 consumer.Poll() 的进一步封装，两个方法都只返回一条消息
				// 区别在于 Poll 还需要自己判断消息类型，而 ReadMessage 只返回消息和错误，更方便判断
				event := consumer.Poll(100)

				if event == nil {
					continue
				}

				switch e := event.(type) {
				case *kafka.Message:
					log.Printf("接收到消息，Message on %s: %s，数据产生时间为 %s \n", e.TopicPartition, string(e.Value), e.Timestamp.Format(time.RFC3339Nano))
					if e.Headers != nil {
						log.Printf("Headers: %v\n", e.Headers)
					}

					// 底层调用的 consumer.StoreOffsets(offsets []TopicPartition)，作用是将给定的 offsets 暂存到本地的 store 库
					// 上层 consumer.StoreMessage(m *Message) 根据 message 计算出要提交的偏移量，然后交给 consumer.StoreOffsets
					// 之后等待 auto.commit.interval.ms 将 store 中的偏移量一块提交到 kafka
					// 或者等待显式调用 consumer.Commit 提交暂存在本地 store 的偏移量，完成后清楚 store 内容
					// 如果显式调用 consumer.CommitMessage 仅提交消息所在分区的 偏移量
					_, err := consumer.StoreMessage(e)

					if err != nil {
						log.Printf("Error store offset after message %s: \n", e.TopicPartition)
					}
				case kafka.Error:
					// Errors should generally be considered informational, the client will try to automatically recover.
					if e.Code() == kafka.ErrAllBrokersDown {

					}
					log.Fatalf("接收到错误，Error: %v: %v\n", e.Code(), e)
				case *kafka.Stats:
					// Stats events are emitted as JSON (as string).
					// Either directly forward the JSON to your
					// statistics collector, or convert it to a
					// map to extract fields of interest.
					// The definition of the statistics JSON
					// object can be found here:
					// https://github.com/edenhill/librdkafka/blob/master/STATISTICS.md
					var stats map[string]interface{}
					json.Unmarshal([]byte(e.String()), &stats)
					log.Printf("接收到消费状态消息，Stats: %v messages (%v bytes) messages consumed\n", stats["rxmsgs"], stats["rxmsg_bytes"])
				case kafka.OffsetsCommitted:
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
