package _chan

import (
	"fmt"
	"reflect"
	"testing"
	"time"
)

/*
	扇入模式
	假如有多个源 Channel 输入、但业务线程只需要监听一个目的 Channel 输出
*/

func Test_fan_in(t *testing.T) {
	start := time.Now()

	<-fanInReflect(
		sig2(10*time.Second),
		sig2(20*time.Second),
		sig2(30*time.Second),
	)

	fmt.Printf("done after %v", time.Since(start))
}

func sig2(after time.Duration) <-chan interface{} {
	c := make(chan interface{})
	go func() {
		time.Sleep(after)
		c <- 1
	}()
	return c
}

func fanInReflect(chans ...<-chan interface{}) <-chan interface{} {
	out := make(chan interface{})
	go func() {
		defer close(out)
		// 构造SelectCase slice
		var cases []reflect.SelectCase
		for _, c := range chans {
			cases = append(cases, reflect.SelectCase{
				Dir:  reflect.SelectRecv,
				Chan: reflect.ValueOf(c),
			})
		}

		// 循环，从cases中选择一个可用的
		for len(cases) > 0 {
			i, v, ok := reflect.Select(cases)
			if !ok { // 此channel已经close
				cases = append(cases[:i], cases[i+1:]...)
				continue
			}
			out <- v.Interface()
		}
	}()
	return out
}
