package array

import (
	"testing"
)

// 实参
func updateItemNormal(a []int64, index int) {
	a[index] = 0
}

// 实参
func updateItemPointer(a *[]int64, index int) {
	(*a)[index] = 0
}

// 实参
func updateItemInterface(i interface{}, index int) {
	a := i.([]int64)
	a[index] = 0
}

// 实参
func updateItemInterfacePointer(i *interface{}, index int) {
	a := (*i).([]int64)
	a[index] = 0
}

func Test_pass(t *testing.T) {

	a := make([]int64, 4)
	a[0] = 1
	a[1] = 1
	a[2] = 1
	a[3] = 1

	updateItemNormal(a, 0)

	t.Log(a)

	updateItemPointer(&a, 1)

	t.Log(a)

	var i interface{} = a
	updateItemInterface(i, 2)

	t.Log(a)

	updateItemInterfacePointer(&i, 3)

	t.Log(a)
}
