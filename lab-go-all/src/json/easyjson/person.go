//go:generate easyjson -all person.go

package _easyjson

type Person struct {
	Name string `json:"name"`
	Age  int    `json:"age"`
}
