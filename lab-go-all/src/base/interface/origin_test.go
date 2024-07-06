package _interface

import (
	"fmt"
	"testing"
)

type Animal interface {
	yell()
}

type Dog struct {
}

func (d *Dog) yell() {
	fmt.Println("wang")
}

func Test_origin(t *testing.T) {
	// 接口的底层数据为实现类的对象指针，所以赋值时要取指针
	var animal Animal = &Dog{}
	animal.yell()

}
