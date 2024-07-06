package _func

import (
	"fmt"
	"testing"
	"time"
)

type f func(op int) int

func timeSpend(innerFunc f) f {
	return func(op int) int {
		start := time.Now()
		result := innerFunc(op)
		fmt.Println("time spend: ", time.Since(start).Milliseconds())
		return result
	}
}

func slowFunc(op int) int {
	time.Sleep(1 * time.Second)
	return op
}

func Test_spend_time_func(t *testing.T) {
	timeSpendSlowFunc := timeSpend(slowFunc)

	slowCalculateResult := timeSpendSlowFunc(100)

	t.Log(slowCalculateResult)
}
