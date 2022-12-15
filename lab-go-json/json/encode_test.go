package json

import (
	jsoniter "github.com/json-iterator/go"
	"os"
	"testing"
)

func Test_encode(t *testing.T) {
	fastApi := jsoniter.ConfigFastest

	person := &Person{
		First:    "",
		Last:     "Last",
		FullName: "FullName",
	}

	file, err := os.Create("./encode.json")
	if err != nil {
		t.Fatal(err)
	}

	err = fastApi.NewEncoder(file).Encode(person)
	if err != nil {
		t.Fatal(err)
	}

}
