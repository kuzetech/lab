package _concurrent

import (
	"context"
	"fmt"
	"github.com/go-kratos/kratos/pkg/sync/errgroup"
	"sync/atomic"
	"testing"
	"time"
)

/*
	如果我们无限制地直接调用 ErrGroup 的 Go 方法，就可能会创建出非常多的 goroutine，太多的 goroutine 会带来调度和 GC 的压力，而且也会占用更多的内存资源

	bilibili 实现了一个扩展的 ErrGroup，可以使用一个固定数量的 goroutine 处理子任务。如果不设置 goroutine 的数量，那么每个子任务都会比较“放肆地”创建一个 goroutine 并发执行
	除了可以控制并发 goroutine 的数量，它还提供了 2 个功能：
		1. cancel，失败的子任务可以 cancel 所有正在执行任务；
		2. recover，而且会把 panic 的堆栈信息放到 error 中，避免子任务 panic 导致的程序崩溃。

	但是，这个库存在一些问题：
		1. 一旦你设置了并发数，超过并发数的子任务需要等到调用者调用 Wait 之后才会执行，而不是只要 goroutine 空闲下来，就去执行。如果不注意这一点的话，可能会出现子任务不能及时处理的情况，这是这个库可以优化的一点。
		2. 存在并发问题。在高并发的情况下，如果任务数大于设定的 goroutine 的数量，并且这些任务被集中加入到 Group 中，这个库的处理方式是把子任务加入到一个数组中，但是，这个数组不是线程安全的，有并发问题
*/

// 下面这个方法测试并发问题，运行这个程序会出现死锁
func Test_errgourp_bilibili(t *testing.T) {

	var g errgroup.Group
	g.GOMAXPROCS(1) // 只使用一个goroutine处理子任务

	var count int64
	g.Go(func(ctx context.Context) error {
		time.Sleep(time.Second) //睡眠5秒，把这个goroutine占住
		return nil
	})

	total := 10000

	for i := 0; i < total; i++ { // 并发一万个goroutine执行子任务，理论上这些子任务都会加入到Group的待处理列表中
		go func() {
			g.Go(func(ctx context.Context) error {
				atomic.AddInt64(&count, 1)
				return nil
			})
		}()
	}

	// 等待所有的子任务完成。理论上10001个子任务都会被完成
	if err := g.Wait(); err != nil {
		panic(err)
	}

	got := atomic.LoadInt64(&count)
	if got != int64(total) {
		panic(fmt.Sprintf("expect %d but got %d", total, got))
	}

}
