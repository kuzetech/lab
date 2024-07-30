package _jsoniter

import (
	"encoding/json"
	jsoniter "github.com/json-iterator/go"
	"strconv"
	"strings"
)

var NumberEncoding = jsoniter.Config{
	EscapeHTML:             true,
	SortMapKeys:            true,
	ValidateJsonRawMessage: true,
	UseNumber:              true,
}.Froze()

var FloatEncoding = jsoniter.ConfigFastest

var StandardEncoding = jsoniter.ConfigCompatibleWithStandardLibrary

func DecodeJsonBytes(b []byte) (interface{}, error) {
	var decoded interface{}
	err := NumberEncoding.Unmarshal(b, &decoded)
	if err != nil {
		return nil, err
	}

	return convertNumbers(decoded), nil
}

func convertNumbers(v interface{}) interface{} {
	switch vv := v.(type) {
	case []interface{}:
		for index, mp := range vv {
			vv[index] = convertNumbers(mp)
		}
	case map[string]interface{}:
		for k, v := range vv {
			vv[k] = convertNumbers(v)
		}
	default:
		v = convertDefault(v)
	}
	return v
}

func convertDefault(v interface{}) interface{} {
	switch vv := v.(type) {
	case json.Number:
		i, err := vv.Int64()
		if err == nil {
			return i
		}

		// judge suffix '.0' then try to parse prefix as a int64
		ok := strings.HasSuffix(vv.String(), ".0")
		if ok {
			i2, err := strconv.ParseInt(vv.String()[:len(vv.String())-2], 10, 64)
			if err != nil {
				panic("try parse prefix as a int64, but failed")
			}
			return i2
		}

		f, err := vv.Float64()
		if err != nil {
			panic("json.Number contain neither integer nor float")
		}
		return f
	default:
		return v
	}
}
