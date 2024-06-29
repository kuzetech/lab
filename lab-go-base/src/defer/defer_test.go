package _defer

import (
	"errors"
	"fmt"
	"log"
	"testing"
)

func test1() int {
	var tmp = 0
	defer func() {
		tmp = 1
	}()
	return tmp
}

func test2() (result int) {
	defer func() {
		result = 1
	}()
	return result
}

func test3() (result error) {
	return result
}

func test4() (result error) {
	defer func() {
		result = errors.New("test")
	}()
	return result
}

func Test_defer(t *testing.T) {
	fmt.Println(test1())
	fmt.Println(test2())
	fmt.Println(test3())
	fmt.Println(test4())

	log.Printf("11111")
	log.Printf("22222")
}
