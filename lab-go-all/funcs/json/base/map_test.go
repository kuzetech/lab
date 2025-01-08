package base

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"testing"
)

func Test_map(t *testing.T) {

	data1 := []byte(`{"begin_time":"1703004645"}`)
	var m1 map[string]string
	err := json.Unmarshal(data1, &m1)
	assert.Nil(t, err)

	data2 := []byte(`{"begin_time":1703004645}`)
	var m2 map[string]string
	err = json.Unmarshal(data2, &m2)
	assert.NotNil(t, err)

}
