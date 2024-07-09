package _concurrent

import (
	"errors"
	"fmt"
	"gopkg.in/tomb.v2"
	"testing"
	"time"
)

func TestTomb(t *testing.T) {
	var to tomb.Tomb

	to.Go(func() error {
		for {
			select {
			case <-time.After(time.Second * 2):
				println(1)
			case <-to.Dying():
				println("down long")
				return errors.New("test2")
			}
		}
	})

	to.Go(func() error {
		time.Sleep(time.Second * 6)
		println("down short")
		return errors.New("test1")
	})

	// 等待所有的协程结束
	err := to.Wait()

	// 仅当所有协程都结束了才返回 nil
	// 如果其中一个协程出现错误，也需要等待其他协程结束了，才会返回第一个协程出现的错误，意思是即使多个协程出错，也只返回第一个错误
	fmt.Println("err =", err)

	// 其他的线程可以监听 tomb.Dying() 获知 group 所有任务执行结束

	println("down main")
}
