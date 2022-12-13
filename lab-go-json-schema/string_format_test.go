package main

import (
	"fmt"
	"github.com/xeipuuv/gojsonschema"
	"log"
	"testing"
)

func Test_string_format(t *testing.T) {
	schemaLoader := gojsonschema.NewStringLoader(`{
		"title": "Product",
	  	"description": "A product from Acme's catalog",
		  "type": "object",
		  "required": [ "productTime"],
		  "properties": {
			"productTime": {
			  	"type": "string",
              	"format": "date"
			}
		  }
	}`)
	schema, err := gojsonschema.NewSchema(schemaLoader)
	if err != nil {
		log.Fatal(err)
	}

	dataLoader := gojsonschema.NewStringLoader(`{
		"productTime": "2012-12-12"
	}`)

	result, err := schema.Validate(dataLoader)
	if err != nil {
		log.Fatal(err)
	}

	if result.Valid() {
		fmt.Printf("The data is valid\n")
	} else {
		fmt.Printf("The data is not valid. see errors :\n")
		for _, desc := range result.Errors() {
			fmt.Printf("- %s\n", desc)
		}
	}
}
