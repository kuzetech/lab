package sort

import (
	"fmt"
	"log"
	"sort"
	"testing"
)

func Test_array_sort1(t *testing.T) {
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

	fmt.Println(family) // [{David 2} {Eve 2} {Alice 23} {Bob 25}]
}

type ErrorItem struct {
	Code  string
	Field string
}

type ErrorItems []ErrorItem

func (s ErrorItems) Len() int      { return len(s) }
func (s ErrorItems) Swap(i, j int) { s[i], s[j] = s[j], s[i] }
func (s ErrorItems) Less(i, j int) bool {
	if s[i].Code == s[j].Code {
		return s[i].Field < s[j].Field
	}
	return s[i].Code < s[j].Code
}

func Test_array_sort2(t *testing.T) {
	errorItems := []ErrorItem{
		{"amiss", "#app2"},
		{"btypeError", "#event2"},
		{"amiss", "#app1"},
		{"btypeError", "#event1"},
	}

	sort.Sort(ErrorItems(errorItems))

	for _, item := range errorItems {
		log.Println(item)
	}
}
