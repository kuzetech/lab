package _nsq

import (
	"fmt"
	"github.com/google/uuid"
	jsoniter "github.com/json-iterator/go"
	"github.com/nsqio/go-nsq"
	"testing"
	"time"
)

func Test_nsq(t *testing.T) {
	config := nsq.NewConfig()
	publisher, _ := nsq.NewProducer("localhost:4150", config) // 假设NSQD运行在本地的4150端口

	v7, _ := uuid.NewV7()

	m := map[string]interface{}{
		"#event":      "#user_login",
		"#log_id":     v7.String(),
		"#time":       time.Now().UnixMilli(),
		"#user_id":    "user-fake517942",
		"#account_id": "account-fake955582",
		"#channel":    "tapdb",
		"test":        "test-test",
	}

	d, _ := jsoniter.Marshal(m)

	c := map[string]interface{}{
		"c": string(d),
	}

	d2, _ := jsoniter.Marshal(c)

	err := publisher.Publish("client_log", d2)
	if err != nil {
		fmt.Println("Publish failed:", err)
		return
	}
	fmt.Println("Message published successfully.")

}
