package slice

import "testing"

func Test_compare(t *testing.T) {
	var s0 []int
	var s1 []int

	/*
		// 这里编译器会直接报错
		if s0 == s1 {
			t.Log("相等")
		}
	*/

	// 切片只能跟 nil 进行比较
	if s0 == nil {
		t.Log("s0 nil")
	}
	if s1 == nil {
		t.Log("s1 nil")
	}
}
