package _concurrent

import (
	"fmt"
	"runtime"
	"testing"
	"time"
)

func runTask(id int) int {
	time.Sleep(100 * time.Millisecond)
	return id
}

func Test_first_finish(t *testing.T) {
	var taskNum int = 10

	// 使用该行写法可能导致程序结束时，其他协程资源无法释放，因为其他协程阻塞在往 chan 中放数据的过程，但是 chan 没有人消费了
	// finishCh := make(chan interface{})

	finishCh := make(chan interface{}, taskNum)

	for i := 0; i < taskNum; i++ {
		go func(id int) {
			finishCh <- runTask(id)
		}(i)
	}

	fmt.Println(<-finishCh)

	time.Sleep(1 * time.Second)
	fmt.Println(runtime.NumGoroutine())
}
