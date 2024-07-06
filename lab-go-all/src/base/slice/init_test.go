package slice

import "testing"

/**
slice 是一个数据结构，类似于 ArrayList
其内部包含：
	1.一个指向数组截取元素的指针
	2.切片能访问的元素个数
	3.从数组截取元素到数组最后一个元素的长度
*/

func Test_create(t *testing.T) {
	arr := [...]int{1, 2, 3, 4, 5}
	t.Log(len(arr), cap(arr))

	// 左闭右开
	arraySlice := arr[0:2]
	t.Log(len(arraySlice), cap(arraySlice)) // 2,5

	var s0 []int
	t.Log(len(s0), cap(s0))

	s1 := []int{1, 2, 3, 4}
	t.Log(len(s1), cap(s1))

	s2 := make([]int, 5) // 初始化 5 个位置
	t.Log(len(s2), cap(s2))

	s3 := make([]int, 3, 5) // 仅初始化 3 个位置
	t.Log(len(s3), cap(s3))

	// 这里会报错，访问切片元素不能超过 length 长度
	// t.Log(s2[3])

	s3 = append(s3, 1)
	t.Log(s3[3])
}
