package objectpool

import (
	"fmt"
	"runtime"
	"sync"
	"testing"
)

/*
	pool 由于其缓存对象生命周期受到 GC 影响，其实不像是对象池，更像缓存池
	也因此不适合做连接池等需要自己管理生命周期资源的池化

	更多适用于通过复用，降低复杂对象的创建和GC代价

	其协程安全，但是会有锁的开销，开发程序时要权衡复用和锁的代价哪个大
*/

func Test_sync_poll(t *testing.T) {
	// 该对象池是线程安全的
	pool := &sync.Pool{
		New: func() interface{} {
			fmt.Println("create a new object")
			return 100
		},
	}

	// 这里对象池没有东西，创建后直接被取出，如果要放回必须手动调用 put 方法
	v1 := pool.Get().(int)
	fmt.Println(v1)

	pool.Put(3)

	// 手动触发 GC，会清除 pool 中缓存的对象
	runtime.GC()

	v2 := pool.Get().(int)
	fmt.Println(v2)
}
