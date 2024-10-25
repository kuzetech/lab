package _string

import (
	"testing"
)

func Test_traverse(t *testing.T) {
	s := "中华人民共和国"

	for _, c := range s {
		// [1] 代表对应哪个位置的参数
		// %x 对应 16进制
		// %d 对应 10进制
		t.Logf("%[1]c %[1]x %[1]d", c)
	}

}
