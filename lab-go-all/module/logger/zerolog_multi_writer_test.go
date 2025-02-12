package logger

import (
	"fmt"
	rotatelogs "github.com/lestrrat-go/file-rotatelogs"
	"github.com/rs/zerolog"
	log "github.com/rs/zerolog/log"
	"os"
	"strings"
	"testing"
	"time"
)

const (
	timeFormat         = "2006-01-02 15:04:05"
	logDir             = "./run_log/"
	currentLogFileName = "study-golang"
)

var Logger zerolog.Logger

func init() {

	// 全局设置
	// zerolog.TimeFieldFormat = timeFormat
	zerolog.SetGlobalLevel(zerolog.InfoLevel)

	log.Info().Msg("创建 log 目录") // 原格式为{"level":"info","time":"2022-09-29T18:28:37+08:00","message":"创建 log 目录"}
	err := os.MkdirAll(logDir, os.ModePerm)
	if err != nil {
		log.Error().Err(err).Msg("")
		return
	}

	// 单独指定 TimeFormat 会覆盖全局设置
	consoleWriter := zerolog.ConsoleWriter{Out: os.Stdout, TimeFormat: timeFormat}
	consoleWriter.FormatLevel = func(i interface{}) string {
		return strings.ToUpper(fmt.Sprintf("%s", i))
	}
	consoleWriter.FormatMessage = func(i interface{}) string {
		return fmt.Sprintf("%s", i)
	}
	consoleWriter.FormatFieldName = func(i interface{}) string {
		return fmt.Sprintf("%s:", i)
	}
	consoleWriter.FormatFieldValue = func(i interface{}) string {
		return fmt.Sprintf("%s;", i)
	}

	logFile := logDir + currentLogFileName
	fileWriter, _ := rotatelogs.New(
		// logFile+".%Y%m%d",                          //每天
		// rotatelogs.WithLinkName(logFile),           //生成软链，指向最新日志文件
		// rotatelogs.WithRotationTime(24*time.Hour),  //最小为1分钟轮询。默认60s  低于1分钟就按1分钟来
		// rotatelogs.WithRotationCount(3),            //设置3份 大于3份 或到了清理时间 开始清理
		// rotatelogs.WithRotationSize(100*1024*1024), //设置100MB大小,当大于这个容量时，创建新的日志文件

		logFile+"-%Y%m%d%H%M.log",                  //每分钟
		rotatelogs.WithLinkName(logFile),           //生成软链，指向最新日志文件
		rotatelogs.WithRotationTime(time.Minute),   //最小为1分钟轮询。默认60s  低于1分钟就按1分钟来
		rotatelogs.WithRotationCount(1),            //设置3份 大于3份 或到了清理时间 开始清理
		rotatelogs.WithRotationSize(100*1024*1024), //设置100MB大小,当大于这个容量时，创建新的日志文件
	)

	multi := zerolog.MultiLevelWriter(consoleWriter, fileWriter)
	Logger = zerolog.New(multi).With().Timestamp().Caller().Logger()
}

func Test_multi_writer(t *testing.T) {
	ticker := time.NewTicker(3 * time.Second)
	for {
		select {
		case <-ticker.C:
			Logger.Info().Msg("1111111")
		}
	}
}
