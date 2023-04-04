package main

import (
	"github.com/gin-gonic/gin"
	"io"
	"io/ioutil"
	"log"
	"net/http"
)

var (
	bodyContentByteLimit           int64           = 10
	bodyContentByteLimitMiddleware gin.HandlerFunc = nil
)

func init() {
	bodyContentByteLimitMiddleware = func(c *gin.Context) {
		reader := io.LimitReader(c.Request.Body, bodyContentByteLimit)

		bodyContent, err := ioutil.ReadAll(reader)

		// 仅读取指定大小的数据， err 不报错
		log.Println(string(bodyContent))
		if err != nil {
			c.Abort()
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
			return
		}

		// 再次读取读到的数据为[], err 不报错
		bodyContent, err = ioutil.ReadAll(reader)
		log.Println(string(bodyContent))
		if err != nil {
			c.Abort()
			c.JSON(http.StatusBadRequest, gin.H{"error": err})
			return
		}

	}
}
