package jsoniter

import (
	jsoniter "github.com/json-iterator/go"
	"os"
	"testing"
)

func Test_encode_to_file(t *testing.T) {
	fastApi := jsoniter.ConfigFastest

	person := &Person{
		First:    "",
		Last:     "Last",
		FullName: "FullName",
	}

	file, err := os.Create("./data-person.json")
	if err != nil {
		t.Fatal(err)
	}

	err = fastApi.NewEncoder(file).Encode(person)
	if err != nil {
		t.Fatal(err)
	}

}
