package flowcontroller

import (
	"context"
	"fmt"
	"golang.org/x/time/rate"
	"testing"
	"time"
)

func Test_token_bucket(t *testing.T) {
	//初始化 limiter 每秒10个令牌，令牌桶容量为20
	limiter := rate.NewLimiter(2, 3)

	for {
		err := limiter.WaitN(context.Background(), 1)
		if err != nil {
			fmt.Println("error", err)
		} else {
			fmt.Println("success get token", time.Now())
		}
	}
}
