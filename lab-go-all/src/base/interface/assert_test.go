package _interface

import (
	"fmt"
	"testing"
)

func showType(v interface{}) {
	// 这里的 t 会变成实际类型
	switch t := v.(type) {
	case int:
		fmt.Print("int", t)
	case string:
		fmt.Print("string", t)
	case struct{}: // 无效，只会匹配具体的对象类型
		fmt.Print("struct")
	case *Dog:
		fmt.Print("*dog")
	case Dog:
		fmt.Print("dog")
	default:
		fmt.Print("unknown")
	}
}

func Test_assert(t *testing.T) {
	d := new(Dog)
	showType(*d)

	var i int = 1
	showType(i)
}
