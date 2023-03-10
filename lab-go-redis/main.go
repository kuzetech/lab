package main

import (
	"context"
	"lab-go-redis/normal"
	"time"
)

func main() {

	client := normal.NewClusterRedisClient()

	sub := client.Subscribe(context.Background(), "555")

	for true {
		select {
		case msg := <-sub.Channel():
			println("接收到消息", msg)
		case <-time.After(time.Second):
			cmd := client.Get(context.Background(), "1")
			if cmd.Err() != nil {
				println("错误")
			} else {
				println("key 的数据为, ", cmd.Val())
			}
		}
	}

}
