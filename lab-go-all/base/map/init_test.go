package _map

import "testing"

/*
	特别注意 map 和 slice 一样是一种可以扩容的结构
	初始化时指定长度，在一定程度上可以提高性能
*/

func Test_init(t *testing.T) {
	m1 := map[string]int{
		"one": 1,
	}
	m1["two"] = 2

	m2 := map[string]int{}
	m2["one"] = 1

	m3 := make(map[string]int)
	t.Log(len(m3)) // 0

	m4 := make(map[string]int, 10)
	t.Log(len(m4)) // 0

	// 无法使用 make 的三参数方法，因为 map 元素没有默认值可以初始化
	// m5 := make(map[string]int, 10, 10)

}
