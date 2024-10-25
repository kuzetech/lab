package array

import "testing"

func Test_compare(t *testing.T) {
	a1 := [3]int{1, 2, 3}
	a2 := [3]int{1, 2, 3}
	a3 := [...]int{1, 2, 3, 4}
	a4 := [...]int{1, 2, 3, 5}

	if a1 == a2 {
		t.Log("a1 a2 相等")
	}

	if a3 == a4 {
		t.Log("a3 a4 相等")
	}

	// 可以比较，必须长度相等，且每一个元素都相等
}
