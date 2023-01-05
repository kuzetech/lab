package main

import (
	"context"
	"fmt"
	"os"
	"strconv"
	"time"

	jsoniter "github.com/json-iterator/go"
	"github.com/robfig/cron/v3"
	"github.com/rs/zerolog/log"
)

const (
	serverCount          = 1
	messageSizePerSecond = 2

	logParentDir     = "./sausage-logs/"
	dayLogDirPathFmt = "%ss%d/%d/%d/%d"
	hourLogPathFmt   = "%ss%d/%d/%d/%d/%d.log"
)

func testHourLevel() {

	currentTime := time.Now()
	cYear, cMonth, cDay := currentTime.Date()
	cHour := currentTime.Hour()

	nextDayTime := currentTime.AddDate(0, 0, 1)
	ndYear, ndMonth, ndDay := nextDayTime.Date()

	nextHourTime := currentTime.Add(time.Hour)
	nhYear, nhMonth, nhDay := nextHourTime.Date()
	nhHour := nextHourTime.Hour()

	for i := 1; i <= serverCount; i++ {
		dirPath1 := fmt.Sprintf(dayLogDirPathFmt, logParentDir, i, cYear, cMonth, cDay)
		CreateDir(dirPath1)
		dirPath2 := fmt.Sprintf(dayLogDirPathFmt, logParentDir, i, ndYear, ndMonth, ndDay)
		CreateDir(dirPath2)

		filePath1 := fmt.Sprintf(hourLogPathFmt, logParentDir, i, cYear, cMonth, cDay, cHour)
		CreateFile(filePath1)
		filePath2 := fmt.Sprintf(hourLogPathFmt, logParentDir, i, nhYear, nhMonth, nhDay, nhHour)
		CreateFile(filePath2)
	}

	c := cron.New(cron.WithSeconds())
	c.AddFunc("0 0 23 * * *", func() {
		log.Info().Msg("每天 23 点整触发创建明天的文件夹")
		wTime := time.Now().AddDate(0, 0, 1)
		wyear, wmonth, wday := wTime.Date()
		for i := 1; i <= serverCount; i++ {
			dirPath := fmt.Sprintf(dayLogDirPathFmt, logParentDir, i, wyear, wmonth, wday)
			CreateDir(dirPath)
		}
	})

	c.AddFunc("0 30 * * * *", func() {
		log.Info().Msg("每个小时的 30 分钟整触发创建下一个小时的文件")
		wTime := time.Now().Add(time.Hour)
		wyear, wmonth, wday := wTime.Date()
		whour := wTime.Hour()
		for i := 1; i <= serverCount; i++ {
			filePath := fmt.Sprintf(hourLogPathFmt, logParentDir, i, wyear, wmonth, wday, whour)
			CreateFile(filePath)
		}
	})

	c.AddFunc("0 40 * * * *", func() {
		log.Info().Msg("每个小时的 45 分钟整触发删除上一个小时的文件")
		wTime := time.Now().Add(-time.Hour)
		wyear, wmonth, wday := wTime.Date()
		whour := wTime.Hour()
		for i := 1; i <= serverCount; i++ {
			filePath := fmt.Sprintf(hourLogPathFmt, logParentDir, i, wyear, wmonth, wday, whour)
			os.Remove(filePath)
		}
	})

	bytes, err := jsoniter.ConfigFastest.Marshal(message)
	if err != nil {
		log.Error().Err(err).Msg("序列化 message 失败")
		return
	}
	log.Info().Msg("一条数据包含 byte 的数量为：" + strconv.Itoa(len(bytes)))

	bytes = append(bytes, []byte("\n")...)
	log.Info().Msg("一条数据加上换行符包含 byte 的数量为：" + strconv.Itoa(len(bytes)))

	var staticInfo = &statistics{
		count: 0,
	}

	c.AddFunc("0 0 * * * *", func() {
		log.Info().Msg("触发新的一小时写入文件")
		ctx, _ := context.WithDeadline(context.Background(), time.Now().Add(time.Hour))
		now := time.Now()
		year, month, day := now.Date()
		hour := now.Hour()
		for j := 1; j <= serverCount; j++ {
			go func(serverId int) {
				ticker := time.NewTicker(time.Second)
				filePath := fmt.Sprintf(hourLogPathFmt, logParentDir, serverId, year, month, day, hour)
				file, _ := os.OpenFile(filePath, os.O_WRONLY|os.O_CREATE|os.O_APPEND, os.ModePerm)
				if err != nil {
					log.Error().Err(err).Msg("打开文件失败")
					return
				}

				for {
					select {
					case <-ctx.Done():
						ticker.Stop()
						file.Close()
						return
					case <-ticker.C:
						for k := 0; k < messageSizePerSecond; k++ {
							file.Write(bytes)
						}
						staticInfo.addCount(uint64(messageSizePerSecond))
					}
				}
			}(j)
		}
	})

	c.Start()

	dl := nextHourTime.Truncate(time.Hour)
	ctx, _ := context.WithDeadline(context.Background(), dl)
	now := time.Now()
	year, month, day := now.Date()
	hour := now.Hour()
	for j := 1; j <= serverCount; j++ {
		go func(serverId int) {
			ticker := time.NewTicker(time.Second)
			filePath := fmt.Sprintf(hourLogPathFmt, logParentDir, serverId, year, month, day, hour)
			file, openFileErr := os.OpenFile(filePath, os.O_WRONLY|os.O_CREATE|os.O_APPEND, os.ModePerm)
			if openFileErr != nil {
				log.Error().Err(openFileErr).Msg("打开文件失败")
				return
			}
			defer func() {
				ticker.Stop()
				file.Close()
			}()
			for {
				select {
				case <-ctx.Done():
					return
				case <-ticker.C:
					for k := 0; k < messageSizePerSecond; k++ {
						file.Write(bytes)
					}
					staticInfo.addCount(uint64(messageSizePerSecond))
				}
			}
		}(j)
	}

	select {}

}
