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
	mserverCount          = 50
	mmessageSizePerSecond = 250

	mlogParentDir      = "./sausage-logs/"
	mhourLogDirPathFmt = "%ss%d/%d/%d/%d/%d"
	mminuteLogPathFmt  = "%ss%d/%d/%d/%d/%d/%d.log"
)

func testMinuteLevel() {

	currentTime := time.Now()
	cYear, cMonth, cDay := currentTime.Date()

	nextHourTime := currentTime.Add(time.Hour)
	nYear, nMonth, nDay := nextHourTime.Date()

	nlTime := currentTime.Add(time.Minute)
	nlYear, nlMonth, nlDay := nlTime.Date()

	for i := 1; i <= mserverCount; i++ {
		dirPath1 := fmt.Sprintf(mhourLogDirPathFmt, mlogParentDir, i, cYear, cMonth, cDay, currentTime.Hour())
		CreateDir(dirPath1)
		dirPath2 := fmt.Sprintf(mhourLogDirPathFmt, mlogParentDir, i, nYear, nMonth, nDay, nextHourTime.Hour())
		CreateDir(dirPath2)

		filePath1 := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, i, cYear, cMonth, cDay, currentTime.Hour(), currentTime.Minute())
		CreateFile(filePath1)
		filePath2 := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, i, nlYear, nlMonth, nlDay, nlTime.Hour(), nlTime.Minute())
		CreateFile(filePath2)
	}

	c := cron.New(cron.WithSeconds())

	c.AddFunc("0 15 * * * *", func() {
		log.Info().Msg("每个小时的 15 分钟整触发创建下一个小时的文件夹")
		wTime := time.Now().Add(time.Hour)
		wyear, wmonth, wday := wTime.Date()
		for i := 1; i <= mserverCount; i++ {
			dirPath := fmt.Sprintf(mhourLogDirPathFmt, mlogParentDir, i, wyear, wmonth, wday, wTime.Hour())
			CreateDir(dirPath)
		}
	})

	c.AddFunc("30 * * * * *", func() {
		log.Info().Msg("每分钟的 30 秒整触发创建下一分钟的文件")
		wTime := time.Now().Add(time.Second * 30)
		wyear, wmonth, wday := wTime.Date()
		for i := 1; i <= mserverCount; i++ {
			filePath := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, i, wyear, wmonth, wday, wTime.Hour(), wTime.Minute())
			CreateFile(filePath)
		}
	})

	c.AddFunc("45 * * * * *", func() {
		log.Info().Msg("每分钟的 45 秒整触发删除5分钟前的文件")
		wTime := time.Now().Add(-time.Minute * 5)
		wyear, wmonth, wday := wTime.Date()
		for i := 1; i <= mserverCount; i++ {
			filePath := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, i, wyear, wmonth, wday, wTime.Hour(), wTime.Minute())
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

	c.AddFunc("0 * * * * *", func() {
		log.Info().Msg("触发新的一分钟写入文件")
		ctx, _ := context.WithDeadline(context.Background(), time.Now().Add(time.Minute))
		now := time.Now()
		year, month, day := now.Date()
		for j := 1; j <= mserverCount; j++ {
			go func(serverId int) {
				ticker := time.NewTicker(time.Second)
				filePath := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, serverId, year, month, day, now.Hour(), now.Minute())
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
						for k := 0; k < mmessageSizePerSecond; k++ {
							file.Write(bytes)
						}
						staticInfo.addCount(uint64(mmessageSizePerSecond))
					}
				}
			}(j)
		}
	})

	c.Start()

	dl := nlTime.Truncate(time.Minute)
	ctx, _ := context.WithDeadline(context.Background(), dl)
	now := time.Now()
	year, month, day := now.Date()
	for j := 1; j <= mserverCount; j++ {
		go func(serverId int) {
			ticker := time.NewTicker(time.Second)
			filePath := fmt.Sprintf(mminuteLogPathFmt, mlogParentDir, serverId, year, month, day, now.Hour(), now.Minute())
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
					for k := 0; k < mmessageSizePerSecond; k++ {
						file.Write(bytes)
					}
					staticInfo.addCount(uint64(mmessageSizePerSecond))
				}
			}
		}(j)
	}

	select {}

}
