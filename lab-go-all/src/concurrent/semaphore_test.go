package _concurrent

import (
	"context"
	"fmt"
	"golang.org/x/sync/semaphore"
	"log"
	"runtime"
	"testing"
	"time"
)

/*
	信号量
	我们一般用信号量保护一组资源，比如数据库连接池、一组客户端的连接、几个打印机资源

	重要方法：
		1. Acquire 方法：可以一次获取多个资源，如果没有足够多的资源，调用者就会被阻塞。可以通过 Context 增加超时或者 cancel 的机制。如果是正常获取了资源，就返回 nil；否则，就返回 ctx.Err()，信号量不改变
		2. Release 方法：相当于 V 操作，可以将 n 个资源释放，返还给信号量
		3. TryAcquire 方法：尝试获取 n 个资源，但是它不会阻塞，要么成功获取 n 个资源，返回 true，要么一个也不获取，返回 false

	除了官方实现，如果你需要一个可以动态更改资源容量的信号量，可以使用第三方实现  marusama/semaphore
*/

var (
	maxWorkers = runtime.GOMAXPROCS(0)                    // worker数量
	sema       = semaphore.NewWeighted(int64(maxWorkers)) //信号量
	task       = make([]int, maxWorkers*4)                // 任务数，是worker的四倍
)

func Test_semaphore(t *testing.T) {

	ctx := context.Background()

	for i := range task {
		// 如果没有worker可用，会阻塞在这里，直到某个worker被释放
		if err := sema.Acquire(ctx, 1); err != nil {
			break
		}

		// 启动worker goroutine
		go func(i int) {
			defer sema.Release(1)
			time.Sleep(100 * time.Millisecond) // 模拟一个耗时操作
			task[i] = i + 1
		}(i)
	}

	// 请求所有的worker,这样能确保前面的worker都执行完
	if err := sema.Acquire(ctx, int64(maxWorkers)); err != nil {
		log.Printf("获取所有的worker失败: %v", err)
	}

	fmt.Println(task)

}
