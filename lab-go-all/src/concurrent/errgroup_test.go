package _concurrent

import (
	"context"
	"errors"
	"fmt"
	"github.com/stretchr/testify/assert"
	"golang.org/x/sync/errgroup"
	"testing"
	"time"
)

/*
	我们经常会碰到需要将一个通用的父任务拆成几个小任务并发执行的场景
	ErrGroup 就是用来应对这种场景的。它和 WaitGroup 有些类似，但是它提供功能更加丰富：
		1. 和 Context 集成
		2. error 向上传播，可以把子任务的错误传递给 Wait 的调用者
*/

func Test_errgroup_simple(t *testing.T) {
	// 返回的 ctx 一旦有一个子任务返回错误，或者所有任务执行完成，这个新 Context 就会被 cancel
	// 特别注意，如果传递给 WithContext 的 ctx 参数，是一个可以 cancel 的 Context 的话，那么，它被 cancel 的时候，并不会终止正在执行的子任务
	eg, ctx := errgroup.WithContext(context.Background())
	assert.NotNil(t, ctx)

	// 启动第一个子任务,它执行成功
	eg.Go(func() error {
		time.Sleep(5 * time.Second)
		fmt.Println("exec #1")
		return nil
	})
	// 启动第二个子任务，它执行失败
	eg.Go(func() error {
		time.Sleep(10 * time.Second)
		fmt.Println("exec #2")
		return errors.New("failed to exec #2")
	})

	// 启动第三个子任务，它执行成功
	eg.Go(func() error {
		time.Sleep(15 * time.Second)
		fmt.Println("exec #3")
		return nil
	})

	// 等所有的子任务都完成后，它才会返回，否则只会阻塞等待。
	// 如果有多个子任务返回错误，它只会返回第一个出现的错误，如果所有的子任务都执行成功，就返回 nil
	if err := eg.Wait(); err == nil {
		fmt.Println("Successfully exec all")
	} else {
		fmt.Println("failed:", err)
	}
}

// 使用一个 result slice 保存子任务的执行结果，这样，通过查询 result，就可以知道每一个子任务的结果了
func Test_errgroup_all_result(t *testing.T) {
	var g errgroup.Group
	var result = make([]error, 3)

	// 启动第一个子任务,它执行成功
	g.Go(func() error {
		time.Sleep(5 * time.Second)
		fmt.Println("exec #1")
		result[0] = nil // 保存成功或者失败的结果
		return nil
	})

	// 启动第二个子任务，它执行失败
	g.Go(func() error {
		time.Sleep(10 * time.Second)
		fmt.Println("exec #2")

		result[1] = errors.New("failed to exec #2") // 保存成功或者失败的结果
		return result[1]
	})

	// 启动第三个子任务，它执行成功
	g.Go(func() error {
		time.Sleep(15 * time.Second)
		fmt.Println("exec #3")
		result[2] = nil // 保存成功或者失败的结果
		return nil
	})

	if err := g.Wait(); err == nil {
		fmt.Printf("Successfully exec all. result: %v\n", result)
	} else {
		fmt.Printf("failed: %v\n", result)
	}

}
