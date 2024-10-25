package _chan

import (
	"fmt"
	"reflect"
	"testing"
)

/**
待处理的 chan 有时候数量很多或者可能是动态生成的
通过反射方式可以便利的处理
*/

func Test_dynamic_chan_number(t *testing.T) {

	var ch1 = make(chan int, 10)
	var ch2 = make(chan int, 10)

	// 创建SelectCase
	var cases = createCases(ch1, ch2)

	// 执行10次select
	for i := 0; i < 10; i++ {
		// 该方法为这些 case 生成一个 select 语句，阻塞直到其中一个条件可用
		// index 为命中 case 的索引号
		// 如果命中的是 recv case， 则 recv 表示接收到的值，ok 表示 chanel 是否被关闭
		index, recv, ok := reflect.Select(cases)

		if recv.IsValid() { // recv case
			fmt.Println("recv:", index, recv, ok)
		} else { // send case
			fmt.Println("send:", index, ok)
		}
	}
}

func createCases(chs ...chan int) []reflect.SelectCase {
	var cases []reflect.SelectCase

	// 创建recv case，如果命中就会尝试接收数据
	// 索引位置 0 1
	for _, ch := range chs {
		cases = append(cases, reflect.SelectCase{
			Dir:  reflect.SelectRecv,
			Chan: reflect.ValueOf(ch),
		})
	}

	// 创建send case，如果命中就会发送数据
	// 索引位置 2 3
	for i, ch := range chs {
		v := reflect.ValueOf(i)
		cases = append(cases, reflect.SelectCase{
			Dir:  reflect.SelectSend,
			Chan: reflect.ValueOf(ch),
			Send: v,
		})
	}

	return cases
}
