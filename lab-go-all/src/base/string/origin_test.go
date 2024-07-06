package _string

import (
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_origin(t *testing.T) {
	assertions := require.New(t)

	var s string
	t.Log(s) // 初始化为默认零值 “”

	s = "hello"
	t.Log(len(s)) // 5 （当内容都是英文字符时，一个 byte 就可以表示一个字符，因此长度 = 5）

	// 其底层实际上是不可变的 byte slice
	// 求取字符串的 len 方法，其实是计算 byte 的长度

	// \x 用于表示字符常量，后面跟着的每一位都是一个十六进制数。如果后面紧跟着两个十六进制数字，则表示一个字节的十六进制值。例如，\x41 表示ASCII码为65 (4 * 16 + 1 * 1)的字符 ‘A’。
	var s1 string = "\x41"
	assertions.Equal("A", s1)

	// 0x 用于表示数字常量，后面跟着的每一位都是一个十六进制数。如果后面紧跟着两个十六进制数字，则表示一个字节的十六进制值。例如，\x41 = 65 (4 * 16 + 1 * 1)。
	var s2 int = 0x41
	assertions.Equal(65, s2)

	// 中文字符 中 = \xE4\xB8\xAD，这里一共 3 字节
	s = "\xE4\xB8\xAD"
	t.Log(s)      // 中
	t.Log(len(s)) // 3

	// 可以用来存储任何的二进制数据
	s = "\xE4\xBA\xBB\xFF"
	t.Log(s) // 只是打印出来可能是乱码

	// 在 unicode 编码中， 				中 = 0x4E2D
	// 在 utf-8   编码中， 				中 = 0xE4B8AD
	// 因此在 string/[]byte 中，表示为 	中 = [\xE4, \xB8, \xAD]

}
