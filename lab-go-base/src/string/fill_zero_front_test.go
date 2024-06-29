package string

import (
	"fmt"
	"log"
	"testing"
)

func Test_fill_zero_front(t *testing.T) {
	var i = 120
	newStr := fmt.Sprintf("%05d", i)
	fmt.Println(newStr) // 00120
}

func TestName(t *testing.T) {
	var content string = "\n"
	bytes := []byte(content)
	log.Println(len(bytes))
}
