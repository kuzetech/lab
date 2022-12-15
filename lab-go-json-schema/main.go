package main

import (
	"encoding/json"
	"log"
)

func main() {

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

	jsonMap := make(map[string]interface{})
	err := json.Unmarshal(schemaData, &jsonMap)

	if err != nil {
		log.Fatal(err)
	}

	propertiesMap := jsonMap["properties"].(map[string]interface{})
	for fieldName, _ := range propertiesMap {
		log.Println(fieldName)
	}

}
