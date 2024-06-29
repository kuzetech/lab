package jsoniter

import (
	jsoniter "github.com/json-iterator/go"
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"log"
	"testing"
)

func Test_unmarshal_bytes(t *testing.T) {
	// The default performance is already several times faster than the standard library
	// 如果直接使用 jsoniter.Marshal() 就使用的这一配置
	// defaultApi := jsoniter.ConfigDefault

	// tries to be 100% compatible with standard library behavior
	// compatibleApi := jsoniter.ConfigCompatibleWithStandardLibrary

	// If you want to have absolutely best performance
	// this will marshal the float with 6 digits precision (lossy), which is significantly faster
	fastApi := jsoniter.ConfigFastest

	bytes, err := ioutil.ReadFile("./data.json")
	if err != nil {
		t.Fatal(err)
	}

	person := &Person{}
	err = fastApi.Unmarshal(bytes, person)
	if err != nil {
		t.Fatal(err)
	}

	t.Log(person.Last)
}

func Test_unmarshal_bytes_Standard(t *testing.T) {
	assertions := require.New(t)
	var dataBytes = []byte(`{"#id": 1.01}`)
	var value map[string]interface{}
	err := jsoniter.ConfigCompatibleWithStandardLibrary.Unmarshal(dataBytes, &value)
	assertions.Nil(err)
	log.Println(value)
}

func Test_unmarshal_bytes_default(t *testing.T) {
	assertions := require.New(t)
	var dataBytes = []byte(`{"#id": 1}`)
	var value map[string]interface{}
	err := jsoniter.ConfigDefault.Unmarshal(dataBytes, &value)
	assertions.Nil(err)
	log.Println(value)
}
