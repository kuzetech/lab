package _func

import "testing"

func sum(nums ...int) int {
	result := 0
	for _, num := range nums {
		result = result + num
	}
	return result
}

func Test_sum(t *testing.T) {
	sumResult := sum(1, 2, 3)

	t.Log(sumResult)
}
