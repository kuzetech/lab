package _sort

import (
	"fmt"
	"sort"
	"testing"
)

func Test_struct_slice(t *testing.T) {
	family := []struct {
		Name string
		Age  int
	}{
		{"Alice", 23},
		{"Bob", 2},
		{"David", 25},
		{"Eve", 2},
	}

	// Sort by age, keeping original order or equal elements.
	sort.SliceStable(family, func(i, j int) bool {
		return len(family[i].Name) < len(family[j].Name)
	})

	fmt.Println(family) // [{Bob 2} {Eve 2} {Alice 23} {David 25}]
}
