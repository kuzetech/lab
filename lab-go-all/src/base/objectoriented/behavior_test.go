package objectoriented

import (
	"fmt"
	"testing"
	"unsafe"
)

func (s Student) showAddress1() {
	fmt.Printf("object address is %x \n", unsafe.Pointer(&s))
}

func (s *Student) showAddress2() {
	fmt.Printf("point address is %x \n", unsafe.Pointer(s))
}

func Test_behavior(t *testing.T) {
	s := Student{"1", 1}
	fmt.Printf("source address is %x \n", unsafe.Pointer(&s))

	s.showAddress1()
	s.showAddress2()

	sp := &s
	sp.showAddress1()
	sp.showAddress2()

	/*
		source address is c0001180c0
		object address is c0001180d8
		point address is c0001180c0
		object address is c0001180f0
		point address is c0001180c0

		从上述的结果可以发现：
		行为无论是定义在对象还是指针上，指针和对象都可以任意调用
		区别在于定义在对象上的方法，实际使用时会发生对象的复制，导致性能损耗，所以一般都定义在指针上
	*/

}
