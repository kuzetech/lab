package main

import (
	"encoding/json"
	"log"
)

type EventPayload struct {
	Time    int64  `json:"t"`  // 时间
	Event   string `json:"e"`  // 事件
	Content string `json:"c"`  // 内容
	ID      string `json:"id"` // 事件 ID
}

func main() {
	var e EventPayload = EventPayload{
		Time:    1692341633,
		Event:   "account",
		ID:      "ddddddddd",
		Content: "{\"__id__\":\"4196996693004128256\",\"account\":\"\",\"ad\":\"\",\"adv\":\"\",\"chan\":\"default\",\"cid\":1,\"device\":\"MS-7D17\",\"event\":\"account\",\"guest\":1,\"ip\":\"10.30.20.10\",\"server_area\":\"CN\",\"sid\":1,\"time\":1692341633}",
	}

	producer := createProducer()
	//consumer := createConsumer()

	bytes, _ := json.Marshal(e)

	// Synchronously publish a single message to the specified topic.
	// Messages can also be sent asynchronously and/or in batches.
	err := producer.Publish(UseTopic, bytes)
	if err != nil {
		log.Fatal(err)
	}

	//// wait for signal to exit
	//sigChan := make(chan os.Signal, 1)
	//signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
	//<-sigChan
	//
	//// Gracefully stop the producer when appropriate (e.g. before shutting down the service)
	//producer.Stop()
	//
	//// Gracefully stop the consumer.
	//consumer.Stop()

}
