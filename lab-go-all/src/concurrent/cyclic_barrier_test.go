package _concurrent

import (
	"context"
	"github.com/marusama/cyclicbarrier"
	"sync"
	"testing"
	"time"
)

/*
	循环栅栏
	CyclicBarrier 允许一组 goroutine 彼此等待，到达一个共同的执行点。同时，因为它可以被重复使用，所以叫循环栅栏。
	具体的机制是，大家都在栅栏前等待，等全部都到齐了，就抬起栅栏放行

	提供如下方法：
		// 等待所有的参与者到达，如果被ctx.Done()中断，会返回ErrBrokenBarrier
		Await(ctx context.Context) error

		// 重置循环栅栏到初始化状态。如果当前有等待者，那么它们会返回ErrBrokenBarrier
		Reset()

		// 返回当前等待者的数量
		GetNumberWaiting() int

		// 参与者的数量
		GetParties() int

		// 循环栅栏是否处于中断状态
		IsBroken() bool
*/

func Test_cyclic_barrier(t *testing.T) {

	var wg sync.WaitGroup
	wg.Add(3)

	// cyclicbarrier.New(10)

	cb := cyclicbarrier.NewWithAction(3, func() error {
		t.Log("每一次到达执行点的时候执行一次")
		return nil
	})

	go func() {
		time.Sleep(1 * time.Second)
		t.Log("1号选手准备就绪")
		cb.Await(context.Background())
		t.Log("1号选手起跑")
		wg.Done()
	}()

	go func() {
		time.Sleep(5 * time.Second)
		t.Log("2号选手准备就绪")
		cb.Await(context.Background())
		t.Log("2号选手起跑")
		wg.Done()
	}()

	go func() {
		time.Sleep(10 * time.Second)
		t.Log("3号选手准备就绪")
		cb.Await(context.Background())
		t.Log("3号选手起跑")
		wg.Done()
	}()

	wg.Wait()
}
