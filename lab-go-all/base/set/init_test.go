package set

import "testing"

func Test_init(t *testing.T) {
	// 由于 go 里面没有 set，我们可以使用 map 来实现

	set := make(map[string]bool)

	set["a"] = true
	set["b"] = true
	set["c"] = true

	if set["c"] {
		t.Log("c exist")
	}

	if !set["d"] {
		t.Log("d not exist")
	}
}
