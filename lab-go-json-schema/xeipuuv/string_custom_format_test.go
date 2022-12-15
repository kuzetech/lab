package xeipuuv

import (
	"fmt"
	"github.com/xeipuuv/gojsonschema"
	"log"
	"strings"
	"testing"
)

// Define the format checker
type RoleFormatChecker struct{}

// Ensure it meets the gojsonschema.FormatChecker interface
func (f RoleFormatChecker) IsFormat(input interface{}) bool {

	asString, ok := input.(string)
	if ok == false {
		return false
	}

	result := strings.HasPrefix(asString, "ROLE_")

	return result
}

func init() {
	// Add it to the library
	gojsonschema.FormatCheckers.Add("role", RoleFormatChecker{})
}

func Test_string_custom_format(t *testing.T) {
	schemaLoader := gojsonschema.NewStringLoader(`{
		"title": "Product",
	  	"description": "A product from Acme's catalog",
		  "type": "object",
		  "required": [ "productRole"],
		  "properties": {
			"productRole": {
			  	"type": "string",
              	"format": "role"
			}
		  }
	}`)
	schema, err := gojsonschema.NewSchema(schemaLoader)
	if err != nil {
		log.Fatal(err)
	}

	dataLoader := gojsonschema.NewStringLoader(`{
		"productRole": "ROLE_test"
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
