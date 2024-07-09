package _timer

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"testing"
	"time"
)

func Test_time_after(t *testing.T) {
	// test 中无法触发 SIGTERM
	ctx, cancel := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer cancel()

loop:
	for true {
		select {
		case point := <-time.After(time.Second):
			log.Println(point.Format(time.RFC3339Nano))
		case <-ctx.Done():
			log.Println("接收到停止信号")
			// select 也算是一层循环，单纯使用 break 不能跳出外层 for 循环
			break loop
		}
	}

	log.Println("程序停止")
}
