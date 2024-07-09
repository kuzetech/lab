package _time

import (
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

func Test_timezone(t *testing.T) {
	specifiedTimeZoneStr := "2023-03-24T17:02:00Z08:00"

	// location, err := time.LoadLocation(time.Local.String())
	location, err := time.LoadLocation(time.UTC.String())
	assert.Nil(t, err)

	// 如果指定了时区，会直接忽略数据中的时区
	// 如果没有满足的 layout，自定义的格式时间需要跟 golang 创建时间一致
	// 2006-01-02T15:04:05.999Z07:00
	result, err := time.ParseInLocation(time.RFC3339, specifiedTimeZoneStr, location)
	assert.Nil(t, err)

	// 东八区 = 1679648520
	// UTC =   1679677320
	assert.Equal(t, int64(1679648520), result.Unix())
}
