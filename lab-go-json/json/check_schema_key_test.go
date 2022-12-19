package json

import (
	jsoniter "github.com/json-iterator/go"
	"io/ioutil"
	"log"
	"strings"
	"testing"
)

func checkKeyWord(key string) bool {
	lowKey := strings.ToLower(key)
	switch lowKey {
	case "allof":
		return false
	case "anyof":
		return false
	case "oneof":
		return false
	case "not":
		return false
	default:
		return true
	}
}

func recursionKeys(any jsoniter.Any) bool {
	for _, key := range any.Keys() {
		log.Println(key)
		if !checkKeyWord(key) {
			return false
		}
		result := recursionKeys(any.Get(key))
		if !result {
			return false
		}
	}
	return true
}

func Test_check_schema_key(t *testing.T) {
	bytes, err := ioutil.ReadFile("./schema.json")
	if err != nil {
		t.Fatal(err)
	}

	fastApi := jsoniter.ConfigFastest
	result := fastApi.Get(bytes)

	recursionKeys(result)
}
