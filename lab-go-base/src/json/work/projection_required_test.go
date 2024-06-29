package work

import (
	"encoding/json"
	json2 "github.com/kuzetech/lab/lab-go-base/json"
	"github.com/stretchr/testify/require"
	"log"
	"testing"
)

type projector2 func(interface{}) interface{}

func copyProjector2(v interface{}) interface{} {
	return v
}

func buildProjector2(decl TypeDecl) projector2 {
	t := decl.Type
	switch t {
	case "object":
		return buildObjectProjector2(decl.Fields)
	case "array":
		return buildArrayProject2(decl.Items)
	default:
		return copyProjector2
	}
}

func buildObjectProjector2(decls []TypeDecl) projector2 {
	projectors := map[string]projector2{}
	for _, schema := range decls {
		projectors[schema.Name] = buildProjector2(schema)
	}
	return func(v interface{}) interface{} {
		m := v.(map[string]interface{})
		obj := map[string]interface{}{}
		for f, p := range projectors {
			i, ok := m[f]
			if ok {
				obj[f] = p(i)
			}
		}
		return obj
	}
}

func buildArrayProject2(decl *TypeDecl) projector2 {
	p := buildProjector2(*decl)
	return func(v interface{}) interface{} {
		vm := v.([]interface{})
		obj := make([]interface{}, 0, len(vm))
		for _, val := range vm {
			obj = append(obj, p(val))
		}
		return obj
	}
}

func TestProjection2(t *testing.T) {

	assertions := require.New(t)

	var schemaBytes = []byte(`{
		"name": "test",
		"type": "object",
		"required": true,
		"fields": [
			{
				"name": "#id",
				"type": "integer",
				"required": true
			},
			{
				"name": "#person",
				"type": "object",
				"required": true,
				"fields": [
					{
						"name": "#age",
						"type": "integer",
						"required": true
					},
					{
						"name": "#name",
						"type": "string",
						"required": true
					},
					{
						"name": "address",
						"type": "string",
						"required": false
					}
				]
			},
			{
				"name": "#friends",
				"type": "array",
				"required": true,
				"items": {
					"type": "object",
					"required": true,
					"fields": [
						{
							"name": "#age",
							"type": "integer",
							"required": true
						},
						{
							"name": "name",
							"type": "string",
							"required": false
						}
					]
				}
			},
			{
				"name": "#likes",
				"type": "array",
				"required": true,
				"items": {
					"type": "string",
					"required": false
				}
			}
		]
	}`)

	var typeDecl = TypeDecl{}
	err := json.Unmarshal(schemaBytes, &typeDecl)
	assertions.Nil(err)

	p := buildProjector2(typeDecl)

	var dataBytes = []byte(`{
		"#id": 1,
		"#person": {
			"#age": 1,
			"#name": "1",
			"address": 1,
			"other": 1
		},
		"#friends": [
			{
				"#age": 1,
				"name": "1",
				"other": 1
			},
			{
				"#age": 2,
				"name": "2",
				"other": "2"
			}
		],
		"#likes": [
			"a",
			"b"
		],
		"other": 1
	}`)

	value, err := json2.DecodeJsonBytes(dataBytes)
	assertions.Nil(err)

	result := p(value)

	log.Println(result)

	//expected := map[string]interface{}{
	//	"#id": 1,
	//	"#person": H{
	//		"age":     1,
	//		"#name":   "1",
	//		"address": 1,
	//	},
	//	"#friends": A{
	//		H{"#age": 1, "name": "1"},
	//		H{"#age": 2, "name": "2"},
	//	},
	//	"likes": A{
	//		"a",
	//		"b",
	//	},
	//}
	//
	//assertions.Equal(expected, result)
}
