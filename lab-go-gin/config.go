package main

import "github.com/gin-gonic/gin"

func init() {
	// 在 debug 模式下，请求会实时加载模板便于开发调试；而 release 模式会直接从内存中获取模板以提升服务性能
	gin.SetMode(gin.ReleaseMode)
}
