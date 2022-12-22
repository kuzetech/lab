package main

import (
	"context"
	"log"
	"os/signal"
	"syscall"
)

func main() {
	// testNormal()
	// testTransaction()
	// testIdempotence()
	// testIdempotenceErr()
	// testTransactionRestart()

	stopCtx, cancel := signal.NotifyContext(context.Background(), syscall.SIGINT, syscall.SIGTERM)
	defer cancel()

	consumer := createTransactionConsumer("pass3")
	defer consumer.Close()

	count := 0

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

			if msg != nil {
				count++
				log.Printf("当前一共收到消息 %d 条", count)
			}
		}
	}

}
