package main

import (
	"context"
	"fmt"
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"os"
	"testing"
	"time"
)

func TestCreateTopic(t *testing.T) {
	adminClient := createAdmin()

	// Contexts are used to abort or limit the amount of time
	// the Admin call blocks waiting for a result.
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	results, err := adminClient.CreateTopics(
		ctx,
		[]kafka.TopicSpecification{
			{
				Topic: "test4",
				// NumPartitions:     2, // 这个参数必须指定且大于0，该客户端直接忽略 broker 配置项 num.partitions
				// ReplicationFactor: 1, // 该参数可以不设置，默认使用 broker 配置项 default.replication.factor
			},
		},
		kafka.SetAdminOperationTimeout(time.Second*5),
	)

	if err != nil {
		t.Fatalf("Failed to create topic: %v\n", err)
	}

	// Print results
	for _, result := range results {
		if result.Error.Code() == kafka.ErrNoError {
			t.Logf("创建 %s 成功 \n", result.Topic)
		} else if result.Error.Code() == kafka.ErrTopicAlreadyExists {
			t.Logf(" %s 已经存在 \n", result.Topic)
		} else {
			t.Fatalf("创建 %s 失败，原因是 %v\n", result.Topic, result.Error.Error())
		}
	}

	adminClient.Close()
}

func TestDescribeConfig(t *testing.T) {
	adminClient := createAdmin()

	// Contexts are used to abort or limit the amount of time
	// the Admin call blocks waiting for a result.
	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	results, err := adminClient.DescribeConfigs(
		ctx,
		[]kafka.ConfigResource{
			{
				Type: kafka.ResourceBroker,
				Name: "num.partitions",
				Config: []kafka.ConfigEntry{
					{
						Name:  "broker_id",
						Value: "1",
					},
				},
			},
			{
				Type: kafka.ResourceBroker,
				Name: "default.replication.factor",
				Config: []kafka.ConfigEntry{
					{
						Name:  "broker_id",
						Value: "1",
					},
				},
			},
		},
		kafka.SetAdminRequestTimeout(time.Second*5),
	)

	if err != nil {
		fmt.Printf("Failed to DescribeConfigs: %s\n", err)
		os.Exit(1)
	}

	// Print results
	for _, result := range results {
		fmt.Printf("%s %s: %s:\n", result.Type, result.Name, result.Error)
		for _, entry := range result.Config {
			// Truncate the value to 60 chars, if needed, for nicer formatting.
			fmt.Printf("%60s = %-60.60s   %-20s Read-only:%v Sensitive:%v\n",
				entry.Name, entry.Value, entry.Source,
				entry.IsReadOnly, entry.IsSensitive)
		}
	}

	adminClient.Close()
}
