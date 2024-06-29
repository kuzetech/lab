package jsoniter

import (
	"context"
	"encoding/json"
	json2 "github.com/kuzetech/lab/lab-go-base/json"
	"github.com/qri-io/jsonschema"
	"github.com/stretchr/testify/require"
	"testing"
)

var contentBytes2 = []byte("{\"name\": \"test\",\"count\": 123}")
var contentSchema = []byte(`{
	"type": "object",
	"properties": {
		"name": {"type": "string"},
		"count": {"type": "number"}
	}
}`)

func Test_error(t *testing.T) {
	should := require.New(t)

	var m map[string]interface{}
	err := json2.NumberEncoding.Unmarshal(contentBytes2, &m)
	should.Nil(err)

	schema := &jsonschema.Schema{}
	err = json.Unmarshal(contentSchema, schema)
	should.Nil(err)

	// m 中的 count 是 json.number 类型
	// schema 中的 count 也是指定的 number 类型
	// 但是校验是却出现如下错误： [/count: 123 type should be number, got string]
	// 为了方便下游处理，我们选择去除 json.number 类型，所以才会有 util.DecodeJsonBytes 这个方法
	state := schema.Validate(context.Background(), m)
	if state.Errs != nil {
		t.Error(*state.Errs)
	}
}
