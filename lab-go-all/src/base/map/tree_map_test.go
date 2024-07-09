package _map

import (
	"github.com/emirpasic/gods/maps/treemap"
	"testing"
)

func Int64Comparator(a, b interface{}) int {
	aAsserted := a.(int64)
	bAsserted := b.(int64)
	switch {
	case aAsserted > bAsserted:
		return 1
	case aAsserted < bAsserted:
		return -1
	default:
		return 0
	}
}

func Test_tree_map(t *testing.T) {
	m := treemap.NewWith(Int64Comparator)
	m.Put(int64(100), "100")
	m.Put(int64(200), "200")
	m.Put(int64(300), "300")

	key, value := m.Floor(int64(350))
	if key != nil {
		t.Log(value)
	} else {
		t.Log("没找到")
	}
}
