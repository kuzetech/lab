package closeChan

import (
	"log"
	"sync"
	"testing"
)

func Test_close_chan(t *testing.T) {
	testChan := make(chan struct{})

	var wg sync.WaitGroup
	wg.Add(1)

	go func() {
		log.Println("启动监听线程")
		for {
			select {
			case <-testChan:
				log.Println("chan 接收到消息")
				wg.Done()
				return
			}
		}
	}()

	log.Println("准备关闭 chan")
	// 关闭 chan 时所有监听该管道的程序都能收到事件
	// 可以继续从管道中获取未消费的元素
	// 但是不能写入元素到管道不然会报错
	close(testChan)

	wg.Wait()
	log.Println("主线程结束")
}
