package base

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

type TimeStruct struct {
	BeginTime time.Time `json:"begin_time"`
}

func Test_time(t *testing.T) {
	var s *TimeStruct

	data := []byte("{}")
	err := json.Unmarshal(data, &s)
	assert.Nil(t, err)
	assert.True(t, s.BeginTime.IsZero())

	data = []byte(`{"begin_time":1703004645}`)
	err = json.Unmarshal(data, &s)
	assert.NotNil(t, err)
	t.Log(err)

	data = []byte(`{"begin_time":"2024-12-19T15:04:05Z"}`)
	err = json.Unmarshal(data, &s)
	assert.Nil(t, err)
}
