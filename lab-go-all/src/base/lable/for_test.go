package _lable

import (
	"log"
	"testing"
	"time"
)

func Test_for(t *testing.T) {
	var count = 0
loop:
	for {
		select {
		default:
			count++
			if count == 3 {
				log.Println("第三次")
				time.Sleep(time.Second * 2)
				break loop
			}
			log.Println(count) // 3 不打印
		}
	}
}
