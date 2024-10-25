package _groutine

import (
	"fmt"
	"testing"
	"time"
)

/**
csp = 通过管道处理并发
*/

func service() string {
	time.Sleep(50 * time.Millisecond)
	return "Done"
}

func otherTask() {
	fmt.Println("working on something")
	time.Sleep(100 * time.Millisecond)
	fmt.Println("task is done")
}

func asyncService() chan string {
	retCh := make(chan string)
	go func() {
		ret := service()
		fmt.Println("returned result")
		retCh <- ret
		fmt.Println("service exited")
	}()
	return retCh
}

func Test_csp(t *testing.T) {
	retCh := asyncService()
	otherTask()
	fmt.Println(<-retCh)

}
