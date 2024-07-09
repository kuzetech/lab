package retry

import (
	"github.com/avast/retry-go/v4"
	"log"
	"net/http"
	"strings"
	"testing"
	"time"
)

func Test_retry(t *testing.T) {
	var retryFunc = func() error {
		_, err := http.Get("http://asdasd")
		if err != nil {
			return err
		}
		return nil
	}

	var callback = func(n uint, err error) {
		log.Printf("#%d: %s \n", n+1, err)
	}

	// 返回 true 就重试
	var retryIf = func(err error) bool {
		if strings.Index(err.Error(), "unsupported protocol scheme") > -1 {
			return false
		}
		if strings.Index(err.Error(), "no such host") > -1 {
			return false
		}
		return true
	}

	err := retry.Do(
		retryFunc,
		retry.Delay(time.Second*3),    // 3秒后重试
		retry.MaxDelay(time.Second*3), // 虽然指定了 delay 但是可能会超时，需要指定 MaxDelay
		retry.Attempts(3),             // 指定重试次数
		retry.OnRetry(callback),       // 每次重试的回调
		retry.RetryIf(retryIf),        // 触发重试的条件
	)

	if err != nil {
		// 错误中包涵每一次重试的结果
		log.Println(err)
	}
}
