package base

import (
	"encoding/json"
	"github.com/stretchr/testify/assert"
	"os"
	"testing"
)

type AccessKey struct {
	Key    string `json:"key"`
	Secret string `json:"secret"`
}

type Metadata struct {
	AccessKey *AccessKey `json:"access_key"`
}

func Test_file(t *testing.T) {

	file, err := os.ReadFile("/Users/huangsw/code/lab/lab-go-all/funcs/json/base/metadata.json")
	assert.Nil(t, err)

	var m *Metadata
	err = json.Unmarshal(file, &m)
	assert.Nil(t, err)

	t.Log(m)
}
