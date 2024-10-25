package _reflect

import (
	"fmt"
	"reflect"
	"testing"
)

func showType(v interface{}) {
	vType := reflect.TypeOf(v)
	vValue := reflect.ValueOf(v)
	switch vType.Kind() {
	case reflect.Int:
		fmt.Println("int", vValue.Int())
	case reflect.String:
		fmt.Println("string", vValue.String())
	case reflect.Struct: // 有效
		fmt.Println("struct")
	case reflect.Pointer: // 有效
		fmt.Println("pointer")
	default:
		fmt.Println("unknown")
	}
}

func Test_parameter_type(t *testing.T) {
	d := new(Dog)
	showType(d)
	showType(*d)
}
