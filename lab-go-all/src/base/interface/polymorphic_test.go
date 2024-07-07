package _interface

import (
	"fmt"
	"testing"
)

// 多态的实现
func showYell(a Animal) {
	fmt.Print("动物发出了 ")
	a.yell()
}

func Test_yell(t *testing.T) {
	showYell(new(Dog))
	showYell(new(Cat))
}
