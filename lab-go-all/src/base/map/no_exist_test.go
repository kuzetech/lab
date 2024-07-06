package _map

import (
	"github.com/stretchr/testify/require"
	"testing"
)

type Student struct {
	name string
}

func Test_no_exist(t *testing.T) {
	assertions := require.New(t)

	var m = map[string]int{"1": 1}

	noExistValue := m["2"]
	// 特别注意：访问的 key 不存在时会返回零值
	assertions.Equal(0, noExistValue)

	var m1 = map[string]Student{}
	assertions.Equal(Student{}, m1["aaa"])
}
