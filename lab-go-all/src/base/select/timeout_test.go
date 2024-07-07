package _select

import (
	"fmt"
	"testing"
	"time"
)

func Test_timeout(t *testing.T) {
	c1 := make(chan interface{})
	c2 := make(chan interface{})

	select {
	case v1 := <-c1:
		fmt.Println("c1", v1)
	case v2 := <-c2:
		fmt.Println("c2", v2)
	case <-time.After(1 * time.Second):
		fmt.Println("timeout")
	}
}
