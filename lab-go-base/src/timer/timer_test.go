package timer

import (
	"fmt"
	"log"
	"testing"
	"time"
)

func Test_timer(t *testing.T) {
	timer := time.NewTimer(5 * time.Second)

	fmt.Println("开始时间：", time.Now().Format("2006-01-02 15:04:05"))

	count := 0
	for {

		// 从t.C中获取数据，此时time.Timer定时器结束。
		<-timer.C
		fmt.Println("timer", time.Now().Format("2006-01-02 15:04:05"))

		count++

		// 如果想再次调用定时器，只能通过调用 Reset() 函数来执行
		timer.Reset(2 * time.Second)
		fmt.Println("调用 reset 重新设置一次timer定时器，并将时间修改为2秒")

		if count > 3 {
			break
		}
	}

	fmt.Println("ok")
}

func Test_timer_stop(t *testing.T) {
	timer := time.NewTimer(time.Second * 5)

	go func() {
		log.Println("携程已启动")
		<-timer.C
		// 无法触发以下语句
		log.Println("接收到事件")
	}()

	// 等待携程启动
	time.Sleep(time.Second * 2)

	// stop 方法并不会产生通知事件
	timer.Stop()

	time.Sleep(time.Second * 1)
	fmt.Println("ok")
}
