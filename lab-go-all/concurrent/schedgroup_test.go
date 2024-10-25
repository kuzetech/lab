package _concurrent

import (
	"context"
	"github.com/mdlayher/schedgroup"
	"log"
	"testing"
	"time"
)

/*

	提供了 worker pool 可以指定任务在某个时间或者某个时间之后执行

	Delay 传入的是一个 time.Duration 参数，它会在 time.Now()+delay 之后执行函数
	Schedule 可以指定明确的某个时间执行

	Wait 方法。这个方法调用会阻塞调用者，直到之前安排的所有子任务都执行完才返回。
		1.如果 Context 被取消，那么，Wait 方法会返回这个 cancel error。在使用 Wait 方法的时候，
		2.如果调用了 Wait 方法，你就不能再调用它的 Delay 和 Schedule 方法，否则会 panic
		3.Wait 方法只能调用一次，如果多次调用的话，就会 panic

	你可能认为，简单地使用 timer 就可以实现这个功能。其实，如果只有几个子任务，使用 timer 不是问题，但一旦有大量的子任务，而且还要能够 cancel，那么，使用 timer 的话，CPU 资源消耗就比较大了。
	所以，schedgroup 在实现的时候，就使用 container/heap，按照子任务的执行时间进行排序，这样可以避免使用大量的 timer，从而提高性能
*/

func Test_schedgroup(t *testing.T) {
	sg := schedgroup.New(context.Background())

	// 设置子任务分别在100、200、300之后执行
	for i := 0; i < 3; i++ {
		n := i + 1
		sg.Delay(time.Duration(n)*100*time.Millisecond, func() {
			log.Println(n) //输出任务编号
		})
	}

	// 等待所有的子任务都完成
	if err := sg.Wait(); err != nil {
		log.Fatalf("failed to wait: %v", err)
	}
}
