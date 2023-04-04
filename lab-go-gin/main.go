package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	_ "net/http/pprof"
	"os"
	"os/signal"
	"syscall"
	"time"
)

func main() {
	multiLog("Starting server... \n")
	multiLog(fmt.Sprintf("当前请求并发限制为 %d \n", limit))

	// Default 使用 Logger 和 Recovery 中间件
	// router := gin.Default()

	// 不使用默认的中间件
	router := gin.New()

	// 设置全局中间件
	router.Use(gin.Recovery())
	router.Use(ginLoggerMiddleware) // 自定义日志中间件

	// 主要用来测试功能
	initGinRouter(router)

	// 展示完整的 restful 风格的路由定义
	initGinRestRouter(router)

	server := &http.Server{
		Addr:           ":8080",
		Handler:        router,
		ReadTimeout:    10 * time.Second,
		WriteTimeout:   10 * time.Second,
		MaxHeaderBytes: 1 << 20,
	}

	go func() {
		err := server.ListenAndServe()
		if err != nil && !errors.Is(err, http.ErrServerClosed) {
			multiLog(fmt.Sprintf("listen err: %s\n", err))
			os.Exit(1)
		}
	}()

	go func() {
		http.ListenAndServe(":6060", nil)
	}()

	quit := make(chan os.Signal, 1)
	// kill (no param) default send syscall.SIGTERM
	// kill -2 is syscall.SIGINT
	// kill -9 is syscall.SIGKILL but can't be catch, so don't need add it
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	multiLog("Shutting down server... \n")

	// The context is used to inform the server it has 5 seconds to finish
	// the request it is currently handling
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := server.Shutdown(ctx); err != nil {
		multiLog(fmt.Sprintf("Server forced to shutdown: %s\n", err))
		os.Exit(1)
	}
	multiLog("Server exiting \n")
}
