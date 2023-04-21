package file

import (
	"fmt"
	jsoniter "github.com/json-iterator/go"
	"github.com/robfig/cron/v3"
	"github.com/rs/zerolog/log"
	"gopkg.in/tomb.v2"
	"os"
	"time"
)

const (
	dayLogDirPathFmt = "%ss%d/%d/%d/%d"
	hourLogPathFmt   = "%ss%d/%d/%d/%d/%d.log"
)

func Run(config *Config) error {

	err := config.checkParameters()
	if err != nil {
		log.Error().Err(err).Msg("config check Parameters error")
		return err
	}

	logParentDir := config.GeneratePath + "/"
	stopErrChan := make(chan error)

	// 设置调度的最小单位为秒级
	c := cron.New(cron.WithSeconds())

	entryID, err := c.AddFunc("0 0 * * * *", func() {
		log.Info().Msg("开始当前小时写入文件任务")

		currentTime := time.Now()

		err = createSpecifiedTimeParentDirAndFile(currentTime, logParentDir, config.ServerSize)
		if err != nil {
			log.Error().Err(err).Msg("create currentTime ParentDir And File error")
			stopErrChan <- err
			return
		}

		var to tomb.Tomb
		for j := 1; j <= config.ServerSize; j++ {

			go (j)
		}
	})

	c.Start()

	c.Entry(entryID).Job.Run()

	<-stopChan

	return nil
}

func writeFile(to *tomb.Tomb, serverId int, logParentDir string, currentTime *time.Time) {
	year, month, day := currentTime.Date()
	hour := currentTime.Hour()
	ticker := time.NewTicker(time.Second)
	filePath := fmt.Sprintf(hourLogPathFmt, logParentDir, serverId, year, month, day, hour)
	file, err := os.OpenFile(filePath, os.O_WRONLY|os.O_CREATE|os.O_APPEND, os.ModePerm)
	if err != nil {
		log.Error().Err(err).Msg("打开文件失败")

		return
	}

	count := 0
	for {
		select {
		case <-to.Dying():
			ticker.Stop()
			file.Close()
			return
		case <-ticker.C:
			for k := 1; k <= config.EventSize; k++ {
				message := GenerateMessage(k)
				bytes, _ := jsoniter.ConfigFastest.Marshal(message)
				for l := 1; l <= eventMessageSizePerSecond; l++ {
					file.Write(bytes)
				}
			}
			count = count + config.EventSize*eventMessageSizePerSecond
			if count == eventMessageSizePerHour {
				ticker.Stop()
				file.Close()
				return
			}
		}
	}
}
