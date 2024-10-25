package _time

import (
	"testing"
	"time"
)

func Test_truncate(t *testing.T) {
	now := time.Now()
	truncateResult := now.Truncate(time.Minute * 5)
	t.Log(truncateResult.Format(time.RFC3339))
}
