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

	// finishCh := make(chan interface{})  // 使用该行写法可能导致其他的协程运行完成后资源无法释放，因为 chan 阻塞
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

func Test_all_finish(t *testing.T) {
	var taskNum int = 10

	finishCh := make(chan interface{})

	for i := 0; i < taskNum; i++ {
		go func(id int) {
			finishCh <- runTask(id)
		}(i)
	}

	for i := 0; i < taskNum; i++ {
		fmt.Println("finish task", <-finishCh)
	}
}
