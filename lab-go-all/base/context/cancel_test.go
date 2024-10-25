package _context

import (
	"context"
	"fmt"
	"sync"
	"testing"
	"time"
)

func isCancel(ctx context.Context) bool {
	select {
	case <-ctx.Done():
		return true
	default:
		return false
	}
}

func Test_cancel(t *testing.T) {
	var wg sync.WaitGroup
	wg.Add(5)

	ctx, cancel := context.WithCancel(context.Background())
	for i := 0; i < 5; i++ {
		go func(i int, ctx context.Context) {
			for {
				if isCancel(ctx) {
					break
				}
				time.Sleep(50 * time.Millisecond)
			}
			fmt.Println(i, "cancelled")
			wg.Done()
		}(i, ctx)
	}

	cancel()
	wg.Wait()
}
