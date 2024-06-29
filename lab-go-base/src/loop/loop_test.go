package loop

import (
	"log"
	"testing"
	"time"
)

func TestBreak(t *testing.T) {
	var count = 0
	for {
	loop:
		select {
		default:
			count++
			if count == 3 {
				log.Println("第三次")
				time.Sleep(time.Second * 2)
				break loop
			}
			log.Println(count)
			time.Sleep(time.Second * 2)
		}
	}
}
