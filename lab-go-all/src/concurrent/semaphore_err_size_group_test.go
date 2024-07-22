package _concurrent

import (
	"testing"
)

/*
	ErrSizedGroup 也是一个信号量工具
	ErrSizedGroup 为 SizedGroup 提供了 error 处理的功能
	比较特殊的是：
		如果设置了 termOnError，子任务出现第一个错误的时候会 cancel Context，而且后续的 Go 调用会直接返回，Wait 调用者会得到这个错误，这相当于是遇到错误快速返回。
		如果没有设置 termOnError，Wait 会返回所有的子任务的错误
*/

func Test_err_size_group(t *testing.T) {

	// groupOption := syncs.Context(context.Background())

	// 具体用法还需要补充
	// groupOption(syncs.TermOnErr())
	// esg := syncs.NewErrSizedGroup(10, groupOption)

	// ErrSizedGroup 和 SizedGroup 设计得不太一致的地方是，SizedGroup 可以把 Context 传递给子任务，
	// 这样可以通过 cancel 让子任务中断执行，但是 ErrSizedGroup 却没有实现。我认为，这是一个值得加强的地方

}
