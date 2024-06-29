package rpc

import (
	"testing"
	"time"
)

func TestRPC(t *testing.T) {
	go runServer()

	// 等待服务启动
	time.Sleep(3 * time.Second)

	runClient()
	callSync()
	callAsync()

}
