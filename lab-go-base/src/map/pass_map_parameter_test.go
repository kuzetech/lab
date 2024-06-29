package _map

import (
	"testing"
)

// 实参
func addItemNormal(m map[string]int64, key string) {
	m[key] = 0
}

// 实参
func addItemPointer(m *map[string]int64, key string) {
	(*m)[key] = 0
}

// 实参
func addItemInterface(i interface{}, key string) {
	m := i.(map[string]int64)
	m[key] = 0
}

// 实参
func addItemInterfacePointer(i *interface{}, key string) {
	m := (*i).(map[string]int64)
	m[key] = 0
}

func Test_pass(t *testing.T) {
	m := make(map[string]int64)

	addItemNormal(m, "1")

	t.Log(m)

	addItemPointer(&m, "2")

	t.Log(m)

	var i interface{} = m
	addItemInterface(i, "3")

	t.Log(m)

	addItemInterfacePointer(&i, "4")

	t.Log(m)
}
