package runtime

import (
	"runtime"
	"testing"
)

func Test_metrics(t *testing.T) {
	// 可以结合 prometheus 上报监控程序
	runtime.NumGoroutine()
}
