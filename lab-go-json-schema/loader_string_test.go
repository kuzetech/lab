package main

import (
	"fmt"
	"github.com/xeipuuv/gojsonschema"
	"log"
	"testing"
)

func Test_loader_string(t *testing.T) {
	schemaLoader := gojsonschema.NewStringLoader(`{
		"title": "Product",
	  	"description": "A product from Acme's catalog",
		  "type": "object",
		  "required": [ "productId"],
		  "properties": {
			"productId": {
			  "description": "The unique identifier for a product",
			  "type": "integer"
			}
		  }
	}`)
	schema, err := gojsonschema.NewSchema(schemaLoader)
	if err != nil {
		log.Fatal(err)
	}

	dataLoader := gojsonschema.NewStringLoader(`{
		"productId": 1
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
