package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	rotatelogs "github.com/lestrrat-go/file-rotatelogs"
	"io"
	"os"
	"time"
)

var (
	logFileDir                          = "./logs/"
	linkLogName                         = "lab-gin"
	multiWriter         io.Writer       = nil
	ginLoggerMiddleware gin.HandlerFunc = nil
)

func init() {
	logFilePath := logFileDir + linkLogName
	logFileWrite, err := rotatelogs.New(
		logFilePath+"-%Y%m%d.log",                  //每天
		rotatelogs.WithLinkName(logFilePath),       //生成软链，指向最新日志文件
		rotatelogs.WithRotationTime(24*time.Hour),  //最小为1分钟轮询。默认60s  低于1分钟就按1分钟来
		rotatelogs.WithRotationCount(3),            //设置3份 大于3份 或到了清理时间 开始清理
		rotatelogs.WithRotationSize(100*1024*1024), //设置100MB大小,当大于这个容量时，创建新的日志文件
	)
	if err != nil {
		multiLog(fmt.Sprintf("rotatelogs init err: %s\n", err))
		os.Exit(1)
	}

	multiWriter = io.MultiWriter(os.Stdout, logFileWrite)

	ginLoggerMiddleware = gin.LoggerWithConfig(gin.LoggerConfig{
		Formatter: func(params gin.LogFormatterParams) string {
			return fmt.Sprintf("[%s] %s %s %s %d %s %s \n",
				params.TimeStamp.Format(time.RFC3339),
				params.ClientIP,
				params.Method,
				params.Path,
				params.StatusCode,
				params.Latency,
				params.ErrorMessage,
			)
		},
		Output: multiWriter,
	})
}

func multiLog(content string) {
	multiWriter.Write([]byte(content))
}
