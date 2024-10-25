package _concurrent

import (
	"context"
	"errors"
	"fmt"
	"github.com/vardius/gollback"
	"testing"
	"time"
)

/*

	gollback 和 Hunch 是属于同一类的并发原语，对一组子任务的执行结果，可以选择一个结果或者多个结果

	gollback 也是用来处理一组子任务的执行的，不过它解决了 ErrGroup 收集子任务返回结果的痛点。
	使用 ErrGroup 时，如果你要收集所有子任务的结果和错误，你需要定义额外的变量收集执行结果和错误，但是这个库可以提供更便利的方式。
	它提供的三个方法，分别是 All、Race 和 Retry
*/

func Test_gollback_all(t *testing.T) {

	// 它会等待所有的异步函数（AsyncFunc）都执行完才返回，而且返回结果的顺序和传入的函数的顺序保持一致。
	// 第一个返回参数是子任务的执行结果，第二个参数是子任务执行时的错误信息

	results, errs := gollback.All(
		context.Background(), // ctx 会被传递给子任务。如果你 cancel 这个 ctx，可以取消子任务
		func(ctx context.Context) (interface{}, error) {
			time.Sleep(3 * time.Second)
			return 1, nil // 第一个任务没有错误，返回1
		},
		func(ctx context.Context) (interface{}, error) {
			return nil, errors.New("failed") // 第二个任务返回一个错误
		},
		func(ctx context.Context) (interface{}, error) {
			return 3, nil // 第三个任务没有错误，返回3
		},
	)

	fmt.Println(results) // 输出子任务的结果
	fmt.Println(errs)    // 输出子任务的错误信息

}

func Test_gollback_race(t *testing.T) {

	// Race 方法跟 All 方法类似，只不过，在使用 Race 方法的时候，任意一个异步函数执行完成就立马返回，而不会返回所有的子任务信息。如果所有的子任务都没有成功，就会返回最后一个 error 信息
	// 如果有一个正常的子任务的结果返回，Race 会把传入到其它子任务的 Context cancel 掉，这样子任务就可以中断自己的执行

	firstResult, lastErr := gollback.Race(
		context.Background(),
		func(ctx context.Context) (interface{}, error) {
			time.Sleep(3 * time.Second)
			fmt.Println("这里不会被执行")
			return 1, nil // 第一个任务没有错误，返回1
		},
		func(ctx context.Context) (interface{}, error) {
			return nil, errors.New("failed")
		},
		func(ctx context.Context) (interface{}, error) {
			return 3, nil // 第三个任务没有错误，返回3
		},
	)

	fmt.Println(firstResult) // 输出子任务的结果
	fmt.Println(lastErr)     // 输出子任务的错误信息

}

func Test_gollback_retry(t *testing.T) {

	// Retry 不是执行一组子任务，而是执行一个子任务。
	// 如果子任务执行失败，它会尝试一定的次数，如果一直不成功 ，就会返回失败错误 ，如果执行成功，它会立即返回。
	// 如果 retires 等于 0，它会永远尝试，直到成功

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	// 尝试5次，或者超时返回
	res, err := gollback.Retry(ctx, 5, func(ctx context.Context) (interface{}, error) {
		return nil, errors.New("failed")
	})

	fmt.Println(res) // 输出结果
	fmt.Println(err) // 输出错误信息

}
