package main

import (
	"github.com/gin-gonic/gin"
	"time"
)

var (
	upstreamParameterMiddleware   gin.HandlerFunc = nil
	downstreamParameterMiddleware gin.HandlerFunc = nil
)

func init() {
	upstreamParameterMiddleware = func(c *gin.Context) {
		c.Set("abc", time.Now().Format(time.RFC3339))
	}

	downstreamParameterMiddleware = func(c *gin.Context) {
		parameter := c.GetString("abc")
		multiLog("从上游获取到的参数为：" + parameter + " \n")
	}
}
