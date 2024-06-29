package reflect

import (
	"github.com/stretchr/testify/require"
	"reflect"
	"testing"
)

func Test_deep_equal(t *testing.T) {
	assertions := require.New(t)

	var a = []string{"a", "b", "c"}
	var b = []string{"c", "b", "a"}

	// 数组要求顺序都一致
	result := reflect.DeepEqual(a, b)
	assertions.Equal(false, result)

	var c = make(map[string]int)
	c["c"] = 1
	c["a"] = 1
	c["b"] = 1

	var d = make(map[string]int)
	d["a"] = 1
	d["b"] = 1
	d["c"] = 1

	// map 要求每一个元素都一样
	result2 := reflect.DeepEqual(c, d)
	assertions.Equal(true, result2)

}
