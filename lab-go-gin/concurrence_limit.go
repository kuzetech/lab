package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
)

var (
	limit                                         = 2
	limitChan                                     = make(chan struct{}, limit)
	ginConcurrenceLimitMiddleware gin.HandlerFunc = nil
)

func init() {
	ginConcurrenceLimitMiddleware = func(c *gin.Context) {
		// 请求前
		limitChan <- struct{}{}
		value := c.Query("a")
		multiLog(fmt.Sprintf("开始执行请求 %s \n", value))

		c.Next()

		// 请求后
		<-limitChan
		multiLog(fmt.Sprintf("请求 %s 结束 \n", value))
	}
}
