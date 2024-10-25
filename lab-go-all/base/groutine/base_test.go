package _groutine

import (
	"fmt"
	"testing"
	"time"
)

func Test_groutine(t *testing.T) {
	for i := 0; i < 10; i++ {
		// 由于是值传递所以能打印 0-9
		go func(j int) {
			fmt.Println(j)
		}(i)
		/*
			// 千万不要这样写，不然传进来的 i 是共享变量，打印出来的都是 9
			go func() {
				fmt.Println(i)
			}()
		*/
	}
	time.Sleep(2 * time.Second)
}
