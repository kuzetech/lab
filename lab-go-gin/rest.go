package main

import (
	"github.com/gin-gonic/gin"
)

func initGinRestRouter(router *gin.Engine) {
	restGroup := router.Group("/rest")

	// 获取单个
	restGroup.GET("/user/:id", func(c *gin.Context) {

	})

	// 获取列表
	restGroup.GET("/user", func(c *gin.Context) {

	})

	// 创建
	restGroup.POST("/user", func(c *gin.Context) {

	})

	// 修改
	restGroup.PUT("/user/:id", func(context *gin.Context) {

	})

	// 删除
	restGroup.DELETE("/user/:id", func(context *gin.Context) {

	})

}
