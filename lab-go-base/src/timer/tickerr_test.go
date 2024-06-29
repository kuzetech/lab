package timer

import (
	"fmt"
	"testing"
	"time"
)

func Test_ticker(t *testing.T) {
	// Ticker 包含一个通道字段C，每隔时间段 d 就向该通道发送当时系统时间。
	// 它会调整时间间隔或者丢弃 tick 信息以适应反应慢的接收者。
	// 如果d <= 0会触发panic。关闭该 Ticker 可以释放相关资源。
	ticker := time.NewTicker(5 * time.Second)

	// 如果不再使用定时器，一定要调用 stop 方法关闭
	defer ticker.Stop()

	count := 0
	for {
		// 每5秒中从chan t.C 中读取一次
		currentTime := <-ticker.C
		fmt.Println("Ticker:", currentTime.Format("2006-01-02 15:04:05"))
		count++
		if count > 3 {
			break
		}
	}

	fmt.Println("ok")
}
