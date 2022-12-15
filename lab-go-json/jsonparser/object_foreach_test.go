package jsonparser

import (
	"github.com/buger/jsonparser"
	"io/ioutil"
	"log"
	"testing"
)

func recursionKeys(contentBytes []byte, upLevel string) {
	jsonparser.ObjectEach(contentBytes, func(key []byte, value []byte, dataType jsonparser.ValueType, offset int) error {
		currentLevel := upLevel + "/" + string(key)
		log.Printf("key 是 %s \n", currentLevel)
		if dataType == jsonparser.Object {
			recursionKeys(value, currentLevel)
		}
		return nil
	})
}

func Test_object_foreach(t *testing.T) {
	contentBytes, err := ioutil.ReadFile("./data.json")
	if err != nil {
		t.Fatal(err)
	}

	// 遍历 /a/b 底下所有的元素
	jsonparser.ObjectEach(contentBytes, func(key []byte, value []byte, dataType jsonparser.ValueType, offset int) error {
		t.Logf("key 是 %s \n", string(key))
		return nil
	}, "person", "name")

	// 递归遍历每个层级下所有的key
	recursionKeys(contentBytes, "")
}
