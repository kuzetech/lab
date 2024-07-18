package _concurrent

import (
	"fmt"
	"golang.org/x/sync/singleflight"
	"sync"
	"testing"
	"time"
)

/*
	SingleFlight 是 Go 开发组提供的一个扩展并发原语。
	它的作用是，在处理多个 goroutine 同时调用同一个函数的时候，只让一个 goroutine 去调用这个函数，
	等到这个 goroutine 返回结果的时候，再把结果返回给这几个同时调用的 goroutine，这样可以减少并发调用的数量

	SingleFlight 主要用在合并并发请求的场景中，尤其是缓存场景
	在 Go 生态圈知名的缓存框架 groupcache 中，就使用了 SingleFlight 解决缓存击穿问题的(大量的请求同时查询一个 key 时，如果这个 key 正好过期失效了，就会导致大量的请求都打到数据库上。这就是缓存击穿)

	它提供了三个方法:
		1.Do：这个方法执行一个函数，并返回函数执行的结果。你需要提供一个 key，对于同一个 key，在同一时间只有一个在执行，同一个 key 并发的请求会等待。
			第一个执行的请求返回的结果，就是它的返回结果。函数 fn 是一个无参的函数，返回一个结果或者 error，而 Do 方法会返回函数执行的结果或者是 error，shared 会指示 v 是否返回给多个请求
		2.DoChan：类似 Do 方法，只不过是返回一个 chan，等 fn 函数执行完，产生了结果以后，就能从这个 chan 中接收这个结果
		3.Forget：告诉 Group 忘记这个 key。这样一来，之后这个 key 请求会执行 f，而不是等待前一个未完成的 fn 函数的结果
*/

func Test_single_flight(t *testing.T) {
	var wg sync.WaitGroup
	wg.Add(10)

	sf := singleflight.Group{}
	for i := 0; i < 10; i++ {
		go func() {
			// do 方法不会返回处理结果，可以在 func 中将结果放入缓存，或者使用 DoChan 方法获取结果
			sf.Do("key", func() (interface{}, error) {
				fmt.Println("我发送了请求")
				time.Sleep(5 * time.Second)
				return 1, nil
			})
			wg.Done()
		}()
	}

	wg.Wait()
	// 最终只打印了一次 '我发送了请求'

}
