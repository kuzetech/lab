package _select

import (
	"log"
	"testing"
	"time"
)

func Test_beyond_timer(t *testing.T) {

	ticker := time.NewTicker(time.Second * 2)
	defer ticker.Stop()

	for true {
		select {
		case currentTime := <-ticker.C:
			log.Println(currentTime)
			time.Sleep(time.Second * 5)
		}
	}

	/*
		2023/02/01 17:08:57 2023-02-01 17:08:57.511875 +0800 CST m=+2.000583663
		2023/02/01 17:09:02 2023-02-01 17:08:59.513644 +0800 CST m=+4.002443477
		2023/02/01 17:09:07 2023-02-01 17:09:03.511969 +0800 CST m=+8.000951370
		2023/02/01 17:09:12 2023-02-01 17:09:09.511626 +0800 CST m=+14.000882956
		2023/02/01 17:09:17 2023-02-01 17:09:13.513256 +0800 CST m=+18.002695456
		2023/02/01 17:09:22 2023-02-01 17:09:17.513864 +0800 CST m=+22.003485884

		ticker.C  =  make(chan Time, 1)
		从上述的数据中猜测，下一次触发 ticker 时如果 chan 中已经有值，放不进去就直接结束，等待下一次触发
	*/

}
