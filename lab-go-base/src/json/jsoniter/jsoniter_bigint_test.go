package jsoniter

import (
	"fmt"
	json2 "github.com/kuzetech/lab/lab-go-base/json"
	"github.com/stretchr/testify/require"
	"log"
	"testing"
)

var data = `{"count": 1621071533109678080123456}`

func TestBigInt(t *testing.T) {
	var result interface{}
	// ConfigFastest marshals float with only 6 digits precision will lose precession
	json2.FloatEncoding.Unmarshal([]byte(data), &result)

	// map[count:1.621071533109678e+18]
	// 这里 count 字段明显是 float64 类型，已经丢失精度
	fmt.Println(result)

	// readNumberAsString
	json2.NumberEncoding.Unmarshal([]byte(data), &result)

	// map[count:1621071533109678080123456]
	// 这里 count 字段实际类型为 json.Number，是 string 的别名
	fmt.Println(result)

}

func TestUseNumber(t *testing.T) {
	assertions := require.New(t)

	a := `{ "id" : 1621071533109678080123456 }`

	var m map[string]interface{}
	err := json2.NumberEncoding.UnmarshalFromString(a, &m)
	assertions.Nil(err)

	// {"count":123}
	log.Println(m)

}
