package _concurrent

import (
	"fmt"
	"math/rand"
	"sync"
	"sync/atomic"
	"testing"
	"time"
)

/*
	atomic 还提供了一个特殊类型 Value
	可以原子性的存储对象，仅限于使用 store 和 load
	因此广泛应用于 配置变更 场景

*/

type Config struct {
	NodeName string
	Addr     string
	Count    int32
}

func loadNewConfig() Config {
	return Config{
		NodeName: "北京",
		Addr:     "10.77.95.27",
		Count:    rand.Int31(),
	}
}

func Test_atomic_config(t *testing.T) {
	var config atomic.Value
	config.Store(loadNewConfig())
	var cond = sync.NewCond(&sync.Mutex{})

	// 模拟不定时获取到新的配置
	go func() {
		for {
			time.Sleep(time.Duration(5+rand.Int63n(5)) * time.Second)
			config.Store(loadNewConfig())
			cond.Broadcast() // 通知等待着配置已变更
		}
	}()

	// 模拟需要重新加载配置的业务线程
	go func() {
		for {
			// 由于 cond 内部维护了一个等待队列，调用 wait 方法时会将协程放入队列
			// 这个时候要求必须持有锁，即必须调用 c.L.Lock()
			cond.L.Lock()
			cond.Wait()                 // 等待变更信号
			c := config.Load().(Config) // 读取新的配置
			cond.L.Unlock()

			fmt.Printf("new config: %+v\n", c)
		}
	}()

	select {}
}
