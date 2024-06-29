package jsoniter

import (
	jsoniter "github.com/json-iterator/go"
	"github.com/stretchr/testify/require"
	"testing"
)

var JsonEncoding = jsoniter.Config{
	EscapeHTML:             true,
	SortMapKeys:            true,
	ValidateJsonRawMessage: true,
	UseNumber:              true,
}.Froze()

func Test_unmarshal_null(t *testing.T) {
	assertions := require.New(t)
	var dataBytes = []byte(`{
		"device_category": "phone",
		"af_sub1": null
	}`)
	var v map[string]interface{}
	err := JsonEncoding.Unmarshal(dataBytes, &v)
	assertions.Nil(err)

	d, e := v["af_sub1"]
	t.Log(e)
	t.Log("\n")
	t.Log(d)

}
