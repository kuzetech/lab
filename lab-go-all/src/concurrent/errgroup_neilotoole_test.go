package _concurrent

import (
	"testing"
)

/*
	如果我们无限制地直接调用 ErrGroup 的 Go 方法，就可能会创建出非常多的 goroutine，太多的 goroutine 会带来调度和 GC 的压力，而且也会占用更多的内存资源

	neilotoole/errgroup 是今年年中新出现的一个 ErrGroup 扩展库，它可以直接替换官方的 ErrGroup，方法都一样，原有功能也一样，只不过增加了可以控制并发 goroutine 的功能

	它的方法集如下：
		type Group
		  func WithContext(ctx context.Context) (*Group, context.Context)
		  func WithContextN(ctx context.Context, numG, qSize int) (*Group, context.Context)
		  func (g *Group) Go(f func() error)
		  func (g *Group) Wait() error


	新增加的方法 WithContextN，可以设置并发的 goroutine 数，以及等待处理的子任务队列的大小。
	当队列满的时候，如果调用 Go 方法，就会被阻塞，直到子任务可以放入到队列中才返回。如果你传给这两个参数的值不是正整数，它就会使用 runtime.NumCPU 代替你传入的参数。

	当然，你也可以把 bilibili 的 recover 功能扩展到这个库中，以避免子任务的 panic 导致程序崩溃
*/

// 下面这个方法测试并发问题，运行这个程序会出现死锁
func Test_errgourp_neilotoole(t *testing.T) {

}
