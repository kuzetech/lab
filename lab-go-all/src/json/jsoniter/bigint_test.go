package json

import (
	"fmt"
	"testing"
)

var data = `{"count": 1621071533109678080123456}`

func Test_BigInt(t *testing.T) {
	var result map[string]interface{}

	FloatEncoding.Unmarshal([]byte(data), &result)

	// map[count:1.621071533109678e+18]
	// 这里 count 字段明显是 float64 类型，已经丢失精度
	fmt.Println(result)

	NumberEncoding.Unmarshal([]byte(data), &result)

	// map[count:1621071533109678080123456]
	// 这里 count 字段实际类型为 json.Number，是 string 的别名
	fmt.Println(result)

	StandardEncoding.Unmarshal([]byte(data), &result)

	// map[count:1.621071533109678e+24]
	fmt.Println(result)

}
