package jsoniter

import (
	jsoniter "github.com/json-iterator/go"
	"os"
	"testing"
)

func Test_decode(t *testing.T) {
	fastApi := jsoniter.ConfigFastest

	file, err := os.Open("./data-person.json")
	if err != nil {
		t.Fatal(err)
	}

	person := &Person{}
	err = fastApi.NewDecoder(file).Decode(person)
	if err != nil {
		t.Fatal(err)
	}

	t.Log(person.FullName)
}
