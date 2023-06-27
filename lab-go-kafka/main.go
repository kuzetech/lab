package main

import (
	"encoding/json"
	"github.com/rs/zerolog/log"
	"time"
)

func TruncateMinuteTime(t int64) int64 {
	eventTime := time.Unix(t, 0)
	truncateTime := eventTime.Truncate(time.Minute)
	return truncateTime.Unix()
}

func main() {

	consumer := createConsumer("pass2")
	errorCount := 0
	messageCount := 0

	logIdMap := make(map[string]int)

	for {
		if messageCount >= 326 {
			break
		}
		msg, err := consumer.ReadMessage(time.Second)
		if err == nil {
			messageCount++
			var messageMap map[string]interface{}
			err := json.Unmarshal(msg.Value, &messageMap)
			if err != nil {
				log.Error().Err(err).Str("source", string(msg.Value)).Msg("Unmarshal message error")
				continue
			}

			dataField, exist := messageMap["data"]
			if !exist {
				log.Error().Str("source", string(msg.Value)).Msg("message field data no exist")
				continue
			}

			dataMap, ok := dataField.(map[string]interface{})
			if !ok {
				log.Error().Str("source", string(msg.Value)).Msg("message field data can not convert to map")
				continue
			}

			timeField, exist := dataMap["#time"]
			if !exist {
				log.Error().Str("source", string(msg.Value)).Msg("data content time field no exist")
				continue
			}

			timeValue, ok := timeField.(float64)
			if !ok {
				log.Error().Str("source", string(msg.Value)).Msg("data content time field can not convert to float64")
				continue
			}

			logId := dataMap["#log_id"].(string)
			log.Info().Int64("truncateTime", TruncateMinuteTime(int64(timeValue))).Interface("data", dataMap).Msg("接收到消息")

			c, exist := logIdMap[logId]
			if exist {
				logIdMap[logId] = c + 1
			} else {
				logIdMap[logId] = 1
			}
		} else {
			log.Error().Err(err).Msg("consumerLoop ReadMessage error")
			errorCount++
			if errorCount >= 3 {
				break
			}
		}
	}

	//consumer.Close()

	for logId, count := range logIdMap {
		if count > 1 {
			log.Error().Str("log_id", logId).Msg("发现重复数据")
		}
	}

	log.Info().Msg("程序结束")
}
