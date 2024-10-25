package _concurrent

import (
	"context"
	"errors"
	"fmt"
	"github.com/aaronjan/hunch"
	"testing"
	"time"
)

/*
	gollback 和 Hunch 是属于同一类的并发原语，对一组子任务的执行结果，可以选择一个结果或者多个结果

	hunch 提供的功能和 gollback 类似，不过它提供的方法更多 all take last retry waterfall
*/

func Test_hunch_all(t *testing.T) {

	// 它会传入一组可执行的函数（子任务），返回子任务的执行结果。
	// 和 gollback 的 All 方法不一样的是，一旦一个子任务出现错误，它就会返回错误信息，执行结果（第一个返回参数）为 nil。

	ctx := context.Background()
	r, err := hunch.All(
		ctx,
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

	fmt.Println(r, err)

}

func Test_hunch_take(t *testing.T) {

	// 可以指定 num 参数，当满足有 num 个子任务正常执行完没有错误，这个方法就会直接返回这几个子任务的结果。
	// 但是只要有任意一个子任务出现错误，它就会返回错误信息，并且执行结果（第一个返回参数）为 nil。

	ctx := context.Background()
	results, err := hunch.Take(
		ctx,
		2,
		func(ctx context.Context) (interface{}, error) {
			time.Sleep(3 * time.Second)
			return 1, nil
		},
		func(ctx context.Context) (interface{}, error) {
			return nil, errors.New("failed")
		},
		func(ctx context.Context) (interface{}, error) {
			return 3, nil
		},
	)

	fmt.Println(results)
	fmt.Println(err)
}

func Test_hunch_last(t *testing.T) {

	// 只返回最后 num 个正常执行的、没有错误的子任务的结果。一旦一个子任务出现错误，它就会返回错误信息，执行结果（第一个返回参数）为 nil。
	// 比如 num 等于 1，那么，它只会返回最后一个无错的子任务的结果

	ctx := context.Background()
	results, err := hunch.Last(
		ctx,
		1,
		func(ctx context.Context) (interface{}, error) {
			time.Sleep(3 * time.Second)
			return 1, nil
		},
		func(ctx context.Context) (interface{}, error) {
			return 2, nil
		},
		func(ctx context.Context) (interface{}, error) {
			return 3, nil
		},
	)

	fmt.Println(results)
	fmt.Println(err)
}

func Test_hunch_retry(t *testing.T) {

	// 它的功能和 gollback 的 Retry 方法的功能一样，如果子任务执行出错，就会不断尝试，直到成功或者是达到重试上限。如果达到重试上限，就会返回错误。
	// 如果 retries 等于 0，它会不断尝试

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	// 尝试5次，或者超时返回
	res, err := hunch.Retry(ctx, 5, func(ctx context.Context) (interface{}, error) {
		return nil, errors.New("failed")
	})

	fmt.Println(res) // 输出结果
	fmt.Println(err) // 输出错误信息
}

func Test_hunch_waterfall(t *testing.T) {
	// 它其实是一个 pipeline 的处理方式，所有的子任务都是串行执行的，前一个子任务的执行结果会被当作参数传给下一个子任务，直到所有的任务都完成，返回最后的执行结果。
	// 一旦一个子任务出现错误，它就会返回错误信息，执行结果（第一个返回参数）为 nil

	ctx := context.Background()
	r, err := hunch.Waterfall(
		ctx,
		func(ctx context.Context, n interface{}) (interface{}, error) {
			return 1, nil
		},
		func(ctx context.Context, n interface{}) (interface{}, error) {
			return n.(int) + 1, nil
		},
		func(ctx context.Context, n interface{}) (interface{}, error) {
			return n.(int) + 1, nil
		},
	)

	fmt.Println(r, err)
}
