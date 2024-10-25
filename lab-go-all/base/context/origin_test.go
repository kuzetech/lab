package _context

import (
	"context"
	"testing"
	"time"
)

/**
一共实现了四个方法:
	1. Deadline() (deadline time.Time, ok bool)
		ok 返回有没有设置 deadline
	2. Done() <-chan struct{}
		返回一个 chan 对象，用于等待关闭通知，被关闭时可以调用 ctx.Err() 方法获取具体关闭原因
	3. Err() error
		未被关闭时，如果调用该方法返回 nil
		关闭时调用会返回具体原因，如：
			var Canceled = errors.New("context canceled")
			var DeadlineExceeded error = deadlineExceededError{}
			timeout context 底层实际是 deadline，所以返回错误一样
	4. Value(key any) any
		返回与此 ctx 关联的 key 的 value
		每一层只能携带一个 key，因此这其实是一个链式结构，查找一个 key 时沿着 parent 往上寻找
*/

func Test_origin(t *testing.T) {
	_, cancelFunc1 := context.WithCancel(context.Background())
	_, cancelFunc2 := context.WithDeadline(context.Background(), time.Now().Add(5*time.Second))
	_, cancelFunc3 := context.WithTimeout(context.Background(), 5*time.Second)

	// 只要 context 使用完毕, 应该尽快调用 cancelFunc 释放 context 资源
	cancelFunc1()
	cancelFunc2()
	cancelFunc3()

}
