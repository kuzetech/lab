package _concurrent

import (
	"sync"
	"testing"
)

func Test_waitgroup(t *testing.T) {
	var wg sync.WaitGroup
	wg.Add(10)

	for i := 0; i < 10; i++ {
		go func() {
			wg.Done()
		}()
	}

	wg.Wait()
}
