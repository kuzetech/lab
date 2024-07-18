package _concurrent

import (
	"errors"
	"fmt"
	"github.com/facebookgo/errgroup"
	"testing"
	"time"
)

/*
	标准库的 WaitGroup 只提供了 Add、Done、Wait 方法，而且 Wait 方法也没有返回子 goroutine 的 error。

	Facebook 提供的 ErrGroup 提供的 Wait 方法可以返回 error，而且可以包含多个 error。
	子任务在调用 Done 之前，可以把自己的 error 信息设置给 ErrGroup。接着，Wait 在返回的时候，就会把这些 error 信息返回给调用者
*/

func Test_waitgroup_facebook(t *testing.T) {

	var g errgroup.Group
	g.Add(3)

	// 启动第一个子任务,它执行成功
	go func() {
		time.Sleep(5 * time.Second)
		fmt.Println("exec #1")
		g.Done()
	}()

	// 启动第二个子任务，它执行失败
	go func() {
		time.Sleep(10 * time.Second)
		fmt.Println("exec #2")
		g.Error(errors.New("failed to exec #2"))
		g.Done()
	}()

	// 启动第三个子任务，它执行成功
	go func() {
		time.Sleep(15 * time.Second)
		fmt.Println("exec #3")
		g.Done()
	}()

	// 等待所有的goroutine完成，并检查error
	if err := g.Wait(); err == nil {
		fmt.Println("Successfully exec all")
	} else {
		fmt.Println("failed:", err)
	}

}
