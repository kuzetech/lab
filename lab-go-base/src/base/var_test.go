package base

import "testing"

type Student struct {
	name string
	age  uint64
}

func TestVar(t *testing.T) {

	var s Student = Student{
		name: "1",
		age:  1,
	}
}
