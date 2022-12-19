package json

import (
	jsoniter "github.com/json-iterator/go"
	"io/ioutil"
	"log"
	"testing"
)

func recursionKeys(any jsoniter.Any, upLevel string) {
	for _, key := range any.Keys() {
		currentLevel := upLevel + "/" + key
		log.Printf("key 是 %s \n", currentLevel)
		keyAny := any.Get(key)
		if keyAny.ValueType() == jsoniter.ObjectValue {
			recursionKeys(keyAny, currentLevel)
		}
		if keyAny.ValueType() == jsoniter.ArrayValue {
			if keyAny.Size() > 0 && keyAny.Get(0).ValueType() == jsoniter.ObjectValue {
				for i := 0; i < keyAny.Size(); i++ {
					itemAny := keyAny.Get(i)
					recursionKeys(itemAny, currentLevel)
				}
			}
		}
	}
}

func Test_traverse_data(t *testing.T) {
	bytes, err := ioutil.ReadFile("./complex.json")
	if err != nil {
		t.Fatal(err)
	}

	fastApi := jsoniter.ConfigFastest
	result := fastApi.Get(bytes)
	recursionKeys(result, "")
}
