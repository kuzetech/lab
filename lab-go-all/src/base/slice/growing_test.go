package slice

import "testing"

func Test_growing(t *testing.T) {

	var s0 []int
	t.Log(len(s0), cap(s0))

	for i := 0; i < 10; i++ {
		s0 = append(s0, i)
		t.Log(len(s0), cap(s0))
	}

	/**
		growing_test.go:8: 0 0
	    growing_test.go:12: 1 1
	    growing_test.go:12: 2 2
	    growing_test.go:12: 3 4
	    growing_test.go:12: 4 4
	    growing_test.go:12: 5 8
	    growing_test.go:12: 6 8
	    growing_test.go:12: 7 8
	    growing_test.go:12: 8 8
	    growing_test.go:12: 9 16
	    growing_test.go:12: 10 16
	*/

	/**

	可以看到如果背后数组的长度不够了，会自动进行扩容，每次扩容 2 倍
	扩容会创建新的数组和新的切片，因此 append 后需要重新赋值
	由于扩容会进行数组的拷贝，一些高性能的场景下应尽量考虑初始大小

	*/

}
