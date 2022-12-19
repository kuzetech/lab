package json

import (
	jsoniter "github.com/json-iterator/go"
	"io/ioutil"
	"log"
	"strings"
	"testing"
)

func getAnyType(any jsoniter.Any) string {
	return strings.ToLower(any.Get("type").ToString())
}

func checkType(any jsoniter.Any) jsoniter.Any {
	typeStr := getAnyType(any)
	if typeStr == "object" {
		return any.Get("properties")
	}
	if typeStr == "array" {
		itemsAny := any.Get("items")
		if getAnyType(itemsAny) == "object" {
			return itemsAny.Get("properties")
		}
	}
	return nil
}

func recursionSchemas(nodeMap map[string]interface{}, any jsoniter.Any, upLevel string) {
	propertiesAny := checkType(any)
	if propertiesAny != nil {
		for _, key := range propertiesAny.Keys() {
			currentLevel := upLevel + "/" + key
			log.Printf("key 是 %s \n", currentLevel)
			nodeMap[currentLevel] = nil
			keyAny := propertiesAny.Get(key)
			recursionSchemas(nodeMap, keyAny, currentLevel)
		}
	}
}

func Test_traverse_schema(t *testing.T) {
	bytes, err := ioutil.ReadFile("./schema.json")
	if err != nil {
		t.Fatal(err)
	}

	fastApi := jsoniter.ConfigFastest
	result := fastApi.Get(bytes)

	existMap := make(map[string]interface{})
	recursionSchemas(existMap, result, "")

	dataBytes, err := ioutil.ReadFile("./schema-data.json")
	if err != nil {
		t.Fatal(err)
	}

	dataMap := make(map[string]interface{})
	jsoniter.Unmarshal(dataBytes, &dataMap)

	recursionMaps(dataMap, "", existMap)

	t.Log(dataMap)
}

func IsObjectTypeOrArrayObject(o interface{}) []map[string]interface{} {
	result := make([]map[string]interface{}, 5)

	if m, ok := o.(map[string]interface{}); ok {
		result = append(result, m)
		return result
	}

	if a, ok := o.([]interface{}); ok && len(a) > 0 {
		if _, ok = a[0].(map[string]interface{}); ok {
			for _, item := range a {
				result = append(result, item.(map[string]interface{}))
			}
			return result
		}
	}
	return result
}

func recursionMaps(dataMap map[string]interface{}, level string, existMap map[string]interface{}) {
	for key, val := range dataMap {
		currentLevel := level + "/" + key
		_, exist := existMap[currentLevel]
		if !exist {
			delete(dataMap, key)
		} else {
			arrayMap := IsObjectTypeOrArrayObject(val)
			for _, item := range arrayMap {
				recursionMaps(item, currentLevel, existMap)
			}
		}
	}
}
