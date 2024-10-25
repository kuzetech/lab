package _time

import (
	"testing"
	"time"
)

func Test_format(t *testing.T) {
	now := time.Now()
	formatResult := now.Format(time.RFC3339)
	t.Log(formatResult)
	// 2024-07-09T10:36:09+08:00
	// 会根据系统的时区转成对应的时间
}
