package qriio

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/qri-io/jsonschema"
	"testing"
)

func Test_simple(t *testing.T) {
	ctx := context.Background()
	var schemaData = []byte(`{
		"title": "Person",
		"type": "object",
		"required": ["firstName", "lastName"],
		"properties": {
			"firstName": {
				"type": "string"
			},
			"lastName": {
				"type": "string"
			},
			"age": {
				"description": "Age in years",
				"type": "integer",
				"minimum": 0
			},
			"friends": {
			  "type" : "array",
			  "items" : { "title" : "REFERENCE", "$ref" : "#" }
			}
		}
	}`)

	rs := &jsonschema.Schema{}
	if err := json.Unmarshal(schemaData, rs); err != nil {
		panic("unmarshal schema error: " + err.Error())
	}

	var validData = []byte(`{
		"firstName" : "George",
		"lastName" : "Michael"
    }`)

	errs, err := rs.ValidateBytes(ctx, validData)
	if err != nil {
		panic(err)
	}

	if len(errs) > 0 {
		fmt.Println(errs[0].Error())
	}

	var invalidData = []byte(`{
		"firstName" : "Jay",
		"friends" : [{
			"firstName" : "Nas"
		}]
    }`)

	errs, err = rs.ValidateBytes(ctx, invalidData)
	if err != nil {
		panic(err)
	}

	if len(errs) > 0 {
		for _, keyError := range errs {
			fmt.Printf("错误路径为：%s，错误信息为：%s \n", keyError.PropertyPath, keyError.Message)
		}
	}

}
