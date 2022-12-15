package jsonparser

import (
	"github.com/buger/jsonparser"
	"io/ioutil"
	"testing"
)

func Test_specified_key(t *testing.T) {
	contentBytes, err := ioutil.ReadFile("./data.json")
	if err != nil {
		t.Fatal(err)
	}

	paths := [][]string{
		[]string{"person", "name", "fullName"},
		[]string{"person", "avatars", "[0]", "url"},
		[]string{"company", "url"}, // 该路径不存在
	}

	jsonparser.EachKey(contentBytes, func(idx int, value []byte, vt jsonparser.ValueType, err error) {
		if err == nil {
			t.Logf("当前遍历的 key 序号为 %d，值为 %s \n", idx, string(value))
		} else {
			// 如果路径不存在是不会触发返回错误的，程序会直接跳过
			t.Logf("当前遍历的 key 序号为 %d，发生了错误 %v \n", idx, err.Error())
		}
	}, paths...)

}
