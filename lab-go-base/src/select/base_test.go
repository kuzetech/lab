package _select

import (
	"log"
	"testing"
)

func Test_base(t *testing.T) {

	var c = make(chan interface{}, 1)

	for {
		select {
		case t := <-c:
			log.Println(t)
		default:
			log.Println(1)
		}
	}
}
