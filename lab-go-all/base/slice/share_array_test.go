package slice

import (
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_share_array(t *testing.T) {
	assertions := require.New(t)

	arr := [6]int{1, 2, 3, 4, 5, 6}

	// 共同指向同一个数组
	s0 := arr[0:3]
	t.Log(len(s0), cap(s0)) // 3 6	(从指向数组的元素开始，到数组最后一个元素的长度，这里从 0 开始，所以是6)

	s1 := arr[1:3]
	t.Log(len(s1), cap(s1)) // 2 5	(从指向数组的元素开始，到数组最后一个元素的长度，这里从 1 开始，所以是5

	// 修改 3 为 111
	arr[2] = 111

	// 由于切片共享同一个数组，相同位置上值也会被修改
	assertions.Equal(s0[2], 111)
	assertions.Equal(s1[1], 111)
}
