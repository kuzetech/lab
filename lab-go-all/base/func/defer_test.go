package _func

import "testing"

func Test_defer_panic(t *testing.T) {
	defer func() {
		t.Log("Clear resources")
	}()

	t.Log("Started")
	panic("fatal error") // defer 会执行
}
