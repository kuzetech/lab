package _easyjson

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"testing"
)

// go get -u github.com/mailru/easyjson/...
// go install github.com/mailru/easyjson/...
// easyjson -all person.go

func Test_base(t *testing.T) {
	person := Person{Name: "Alice", Age: 30}

	// 序列化
	jsonBytes, err := person.MarshalJSON()
	assert.Nil(t, err)

	fmt.Println("序列化后的 JSON 数据:", string(jsonBytes))

	// 反序列化
	var newPerson Person
	err = newPerson.UnmarshalJSON(jsonBytes)
	assert.Nil(t, err)

	fmt.Println("反序列化后的结构体:", newPerson)
}