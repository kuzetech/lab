package main

import (
	"log"
	"time"
)

func main() {

	consumer := createTransactionConsumer("data")

	go func() {
		for {
			log.Printf("开始接收消息 \n")
			msg, err := consumer.ReadMessage(-1)

			if err != nil {
				log.Printf("接收消息时发生错误 %v \n", err)
				continue
			}

			if msg != nil {
				log.Printf("收到消息")
			}
		}
	}()

	time.Sleep(time.Second * 5)
	log.Printf("5秒睡眠结束，程序开始关闭")

	err := consumer.Close()
	if err != nil {
		log.Fatalf("关闭消费者时发生错误 %v \n", err)
	}
	log.Printf("成功关闭消费者")
}
