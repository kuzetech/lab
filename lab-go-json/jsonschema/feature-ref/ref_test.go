package featureref

import (
	"fmt"
	"github.com/xeipuuv/gojsonschema"
	"log"
	"testing"
)

func Test_ref(t *testing.T) {
	schemaLoader := gojsonschema.NewReferenceLoader("file://./product.schema.json")
	schema, err := gojsonschema.NewSchema(schemaLoader)
	if err != nil {
		log.Fatal(err)
	}

	dataLoader := gojsonschema.NewReferenceLoader("file://./data.json")
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
