package _concurrent

import (
	"context"
	"fmt"
	"github.com/go-pkgz/syncs"
	"sync/atomic"
	"testing"
	"time"
)

/*
	SizedGroup 也是一个信号量工具
	内部是使用信号量和 WaitGroup 实现的，它通过信号量控制并发的 goroutine 数量，或者是不控制 goroutine 数量，只控制子任务并发执行时候的数量（通过）


*/

func Test_size_group(t *testing.T) {

	// 默认情况下，SizedGroup 控制的是子任务的并发数量，而不是 goroutine 的数量。
	// 在这种方式下，每次调用 Go 方法都不会被阻塞，而是新建一个 goroutine 去执行
	// 设置goroutine数是10
	swg := syncs.NewSizedGroup(10)

	// 如果想控制 goroutine 的数量，可以使用 syncs.Preemptive 设置这个并发原语的可选项。
	// 如果设置了这个可选项，但在调用 Go 方法的时候没有可用的 goroutine，那么调用者就会等待，直到有 goroutine 可以处理这个子任务才返回，这个控制在内部是使用信号量实现的
	// swg := syncs.NewSizedGroup(10, syncs.Preemptive)

	var c uint32

	// 执行1000个子任务，只会有10个goroutine去执行
	for i := 0; i < 1000; i++ {
		// 可以把 Context 传递给子任务，这样可以通过 cancel 让子任务中断执行
		swg.Go(func(ctx context.Context) {
			time.Sleep(5 * time.Millisecond)
			atomic.AddUint32(&c, 1)
		})
	}

	// 等待任务完成
	swg.Wait()
	// 输出结果
	fmt.Println(c)

}
