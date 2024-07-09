package _sort

import (
	"fmt"
	"sort"
	"testing"
)

func Test_int_slice(t *testing.T) {
	s := make([]int, 5)
	s[0] = 1
	s[1] = 5
	s[2] = 6
	s[3] = 2
	s[4] = 14

	fmt.Printf("strSlice: %v\n", s)
	sort.Ints(s)
	fmt.Printf("strSlice: %v\n", s)
}
