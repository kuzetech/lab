package _concurrent

import (
	"fmt"
	"testing"
)

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
