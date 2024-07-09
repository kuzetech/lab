package _string

import (
	"fmt"
	"testing"
)

func Test_fill_zero_front(t *testing.T) {
	var i = 120
	fillResult := fmt.Sprintf("%05d", i)
	t.Log(fillResult) // 00120
}
