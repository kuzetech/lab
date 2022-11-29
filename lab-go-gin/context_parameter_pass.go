package main

import (
	"github.com/gin-gonic/gin"
	"time"
)

var (
	upstreamMiddleware   gin.HandlerFunc = nil
	downstreamMiddleware gin.HandlerFunc = nil
)

func init() {
	upstreamMiddleware = func(c *gin.Context) {
		c.Set("abc", time.Now().Format(time.RFC3339))
	}

	downstreamMiddleware = func(c *gin.Context) {
		parameter := c.GetString("abc")
		multiLog("从上游获取到的参数为：" + parameter + " \n")
	}
}
