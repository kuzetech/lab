package _groutine

import (
	"sync"
	"testing"
	"time"
)

func Test_lock(t *testing.T) {
	var m sync.Mutex
	var sum int = 0
	for i := 0; i < 5000; i++ {
		go func() {
			defer func() {
				m.Unlock()
			}()
			m.Lock()
			sum++
		}()
	}

	time.Sleep(2 * time.Second)
	t.Log(sum)
}
