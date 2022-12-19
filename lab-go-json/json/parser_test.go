package json

import (
	jsoniter "github.com/json-iterator/go"
	"io/ioutil"
	"testing"
)

func Test_parser(t *testing.T) {
	fastApi := jsoniter.ConfigFastest

	bytes, err := ioutil.ReadFile("./complex.json")
	if err != nil {
		t.Fatal(err)
	}

	// 返回的类型 any 很方便，可以遍历 key；也可以随意转换 类型
	result := fastApi.Get(bytes, "person", "avatars", 0)
	keys := result.Keys()
	for _, key := range keys {
		t.Log(key)
	}

	result2 := fastApi.Get(bytes, "person", "avatars", 0, "url")
	value := result2.ToString()
	t.Log(value)

	result3 := fastApi.Get(bytes, "test")
	if result3.ValueType() == jsoniter.InvalidValue {
		t.Log("test 节点不存在")
	}

}
