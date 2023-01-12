package main

import (
	"github.com/confluentinc/confluent-kafka-go/kafka"
	"log"
	"sync"
	"time"
)

func main() {

	var wg sync.WaitGroup
	var termChan = make(chan struct{}, 0)

	consumer := createTransactionConsumer("data")

	wg.Add(1)
	go func() {
		log.Printf("开始接收消息 \n")
		for {
			select {
			case <-termChan:
				log.Printf("接收消息线程收到停止信号 \n")
				wg.Done()
				return
			default:
				msg, err := consumer.ReadMessage(100)

				if err != nil {
					if err.(kafka.Error).Code() == kafka.ErrTimedOut {
						continue
					} else {
						log.Printf("接收消息时发生错误 %v \n", err)
						continue
					}
				}

				if msg != nil {
					log.Printf("收到消息")
				}
			}
		}
	}()

	time.Sleep(time.Second * 3)
	log.Printf("睡眠结束，程序开始关闭")

	close(termChan)
	wg.Wait()

	err := consumer.Close()
	if err != nil {
		log.Fatalf("关闭消费者时发生错误 %v \n", err)
	}
	log.Printf("成功关闭消费者")
}
