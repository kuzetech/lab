package _interface

import (
	"fmt"
	"go/types"
	"testing"
	"unsafe"
)

func showType(v interface{}) {
	// 这里的 t 会变成实际类型
	switch t := v.(type) {
	case int:
		fmt.Println("int", t)
	case string:
		fmt.Println("string", t)
	case struct{}: // 无效，只会匹配具体的对象类型
		fmt.Println("struct")
	case types.Pointer: // 无效，只会匹配具体的对象类型
		fmt.Println("type pointer")
	case unsafe.Pointer: // 无效，只会匹配具体的对象类型
		fmt.Println("unsafe pointer")
	case *Dog:
		fmt.Println("*dog")
	case Dog:
		fmt.Println("dog")
	default:
		fmt.Println("unknown")
	}
}

func Test_parameter_type(t *testing.T) {
	d := new(Dog)
	showType(d)
	showType(*d)

	// 如果只想判断是 struct 或者 pointer 类型则需要使用反射
}
