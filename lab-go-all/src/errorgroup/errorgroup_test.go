package errorgroup

import (
	"context"
	"errors"
	"fmt"
	"golang.org/x/sync/errgroup"
	"os"
	"os/signal"
	"syscall"
	"testing"
	"time"
)

func TestErrorGroup(t *testing.T) {
	ctx, cancel := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer cancel()

	eg, ctx := errgroup.WithContext(ctx)

	eg.Go(func() error {
		for {
			select {
			case <-time.After(time.Second * 2):
				println(1)
			case <-ctx.Done():
				println("down long")
				return errors.New(ctx.Err().Error())
			}
		}
	})

	eg.Go(func() error {
		time.Sleep(time.Second * 5)
		println("down short")
		return errors.New("test1")
	})

	// 等待所有的协程结束
	err := eg.Wait()

	// 仅当所有协程都结束了才返回 nil
	// 如果其中一个协程出现错误，也需要等待其他协程结束了，才会返回第一个协程出现的错误，意思是即使多个协程出错，也只返回第一个错误
	fmt.Println("err =", err)

	// 其他的线程可以监听 ctx.Done() 获知 group 所有线程执行结束

	println("down main")

}
