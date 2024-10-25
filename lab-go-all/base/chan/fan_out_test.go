package _chan

import (
	"testing"
)

/*
	扇出模式
	假如只有一个输入源 Channel，有多个目标 Channel, 经常用在设计模式中的观察者模式中
	当一个对象的状态发生变化时，所有依赖于它的对象都会得到通知并自动刷新
*/

func Test_fan_out(t *testing.T) {

}

// 从源 Channel 取出一个数据后，依次发送给目标 Channel。
// 在发送给目标 Channel 的时候，可以同步发送，也可以异步发送
func fanOut(ch <-chan interface{}, out []chan interface{}, async bool) {
	go func() {
		defer func() { //退出时关闭所有的输出chan
			for i := 0; i < len(out); i++ {
				close(out[i])
			}
		}()

		for v := range ch { // 从输入chan中读取数据
			v := v
			for i := 0; i < len(out); i++ {
				i := i
				if async { //异步
					go func() {
						out[i] <- v // 放入到输出chan中,异步方式
					}()
				} else {
					out[i] <- v // 放入到输出chan中，同步方式
				}
			}
		}
	}()
}
