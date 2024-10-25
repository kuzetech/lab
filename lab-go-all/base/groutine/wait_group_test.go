package _groutine

import (
	"sync"
	"testing"
)

func Test_wait_group(t *testing.T) {
	var m sync.Mutex
	// waitGroup 会被传递到不同的协程，然后执行 done 操作，所以推测是线程安全的
	var wg sync.WaitGroup

	var sum int = 0

	wg.Add(5000)
	for i := 0; i < 5000; i++ {
		go func() {
			defer func() {
				m.Unlock()
			}()
			m.Lock()
			sum++
			wg.Done()
		}()
	}

	wg.Wait()
	t.Log(sum)
}
