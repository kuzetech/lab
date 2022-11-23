package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
)

func createConsumer(topic string) *kafka.Consumer {
	c, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers":        "localhost:9092",
		"group.id":                 "myGroup",
		"session.timeout.ms":       6000,
		"auto.offset.reset":        "earliest",
		"enable.auto.offset.store": false, // 关闭自动保存 offset 到本地 store
		"enable.auto.commit":       false,
		"auto.commit.interval.ms":  5000,

		// Statistics output from the client enabled by setting statistics.interval.ms
		// and can handling kafka.Stats events (see below).
		"statistics.interval.ms": 5000,
	})

	if err != nil {
		log.Fatalf("创建 consumer 时失败，原因是：%s \n", err)
	}

	// Subscribe 类型的接口只能调用一次
	// 多次调用后以最后一次订阅的 topic 为准
	err = c.Subscribe(topic, func(c *kafka.Consumer, event kafka.Event) error {
		switch ev := event.(type) {
		case kafka.AssignedPartitions:
			// consumer 一开始启动订阅时会收到该事件
			log.Printf("%s rebalance: %d new partition(s) assigned: %v\n", c.GetRebalanceProtocol(), len(ev.Partitions), ev.Partitions)

			// The application may update the start .Offset of each
			// assigned partition and then call IncrementalAssign().
			// Even though this example does not alter the offsets we
			// provide the call to IncrementalAssign() as an example.
			/*assignErr := c.IncrementalAssign(ev.Partitions)
			if assignErr != nil {
				panic(err)
			}*/

		case kafka.RevokedPartitions:
			// consumer close 时会接收到该事件
			log.Printf("%s rebalance: %d partition(s) revoked: %v\n", c.GetRebalanceProtocol(), len(ev.Partitions), ev.Partitions)
			if c.AssignmentLost() {
				// Our consumer has been kicked out of the group and the entire assignment is thus lost.
				log.Printf("Current assignment lost!\n")
			}

			// The client automatically calls IncrementalUnassign() unless
			// the callback has already called that method.

		}

		return nil
	})

	if err != nil {
		log.Fatalf("consumer 订阅 topic %s 时失败，原因是：%s \n", topic, err)
	}

	return c
}

func createTransactionConsumer(topic string) *kafka.Consumer {
	c, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers":        "localhost:9092",
		"group.id":                 "myGroup",
		"session.timeout.ms":       6000,
		"auto.offset.reset":        "earliest",
		"enable.auto.offset.store": false, // 关闭自动保存 offset 到本地 store
		"enable.auto.commit":       false,
		"auto.commit.interval.ms":  5000,
		"isolation.level":          "read_committed", // 如果生产者没有开启事务也能照常接收数据
	})

	if err != nil {
		log.Fatalf("创建 consumer 时失败，原因是：%s \n", err)
	}

	// Subscribe 类型的接口只能调用一次
	// 多次调用后以最后一次订阅的 topic 为准
	err = c.Subscribe(topic, nil)

	if err != nil {
		log.Fatalf("consumer 订阅 topic %s 时失败，原因是：%s \n", topic, err)
	}

	return c
}
