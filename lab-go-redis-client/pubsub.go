package main

import (
	"context"
	"fmt"
	"time"
)

var SubChannel = "chan"

func testPub() {
	ctx, cancelFunc := context.WithTimeout(context.Background(), time.Second*3)
	defer cancelFunc()

	rdb := NewRedisClientByOption().Client
	err := rdb.Publish(ctx, SubChannel, time.Now().Format(time.RFC3339)).Err()

	if err != nil {
		panic(err)
	}
}

func testSub() {
	ctx, cancelFunc := context.WithTimeout(context.Background(), time.Second*3)
	defer cancelFunc()

	rdb := NewRedisClientByOption().Client
	sub := rdb.Subscribe(ctx, SubChannel)
	defer sub.Close()

	ch := sub.Channel()

	for msg := range ch {
		fmt.Println(msg.Channel, msg.Payload)
	}

}
