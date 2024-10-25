package array

import "testing"

func Test_init(t *testing.T) {
	// 创建
	var a [3]int // 会被初始化为零值
	a[0] = 1

	b := [3]int{1, 2, 3}
	c := [...]int{1, 2, 3}

	b[0] = 1
	c[0] = 1

	// c := [2][2]int{{1, 2}, {3, 4}}

	// 数组元素没办法使用 append 方法，也没办法进行扩容
	// d := append(b, 1)
}
