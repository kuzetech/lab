package _chan

import (
	"fmt"
	"testing"
	"time"
)

func Test_block_chan(t *testing.T) {
	c := make(chan interface{})
	go func() {
		select {
		case v := <-c:
			fmt.Println("接收到了数据", v)
		}
	}()

	time.Sleep(1 * time.Second)

	go func() {
		c <- 5
	}()

	time.Sleep(1 * time.Second)
}
