package _chan

import (
	"fmt"
	"sync"
	"testing"
)

func dataReceiver(ch chan int, wg *sync.WaitGroup, id string) {
	go func() {
		// 如果仅接收 data，通道关闭时会返回零值
		data, ok := <-ch
		if ok {
			fmt.Println("通道未关闭，接收到数据", data)
		} else {
			fmt.Println("通道关闭，接收到数据")
		}
		fmt.Println(id)
		wg.Done()
	}()
}

func Test_close_chan(t *testing.T) {
	ch := make(chan int, 10)

	var wg sync.WaitGroup
	wg.Add(2)

	dataReceiver(ch, &wg, "1")
	dataReceiver(ch, &wg, "2")

	close(ch)

	// 向关闭的 chan 发送数据会导致 panic

	wg.Wait()
}
