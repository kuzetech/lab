package _jsonparser

import (
	"errors"
	"github.com/buger/jsonparser"
	"io/ioutil"
	"testing"
)

func Test_base(t *testing.T) {
	contentBytes, err := ioutil.ReadFile("./data.json")
	if err != nil {
		t.Fatal(err)
	}

	// You can specify key path by providing arguments to Get function
	jsonparser.Get(contentBytes, "person", "name", "fullName")

	// When you try to get object, it will return you []byte slice pointer to data containing it
	// In `company` it will be `{"name": "Acme"}`
	jsonparser.Get(contentBytes, "company")

	// There is `GetInt` and `GetBoolean` helpers if you exactly know key data type
	jsonparser.GetInt(contentBytes, "person", "github", "followers")

	// Or use can access fields by index!
	jsonparser.GetString(contentBytes, "person", "avatars", "[0]", "url")

	// You can use `ArrayEach` helper to iterate items [item1, item2 .... itemN]
	jsonparser.ArrayEach(contentBytes, func(value []byte, dataType jsonparser.ValueType, offset int, err error) {
		val, err := jsonparser.GetString(value, "url")
		if err == nil {
			t.Log(val)
		}
	}, "person", "avatars")

	// If the key doesn't exist it will throw an error
	_, err = jsonparser.GetInt(contentBytes, "company", "size")
	if err != nil && errors.Is(err, jsonparser.KeyPathNotFoundError) {
		t.Log("找不到路径")
	} else {
		t.Fatal(err)
	}

}
