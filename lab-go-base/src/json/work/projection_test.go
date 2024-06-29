package work

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

type projector func(interface{}) interface{}

func copyProjector(v interface{}) interface{} {
	return v
}

func buildProjector(schema map[string]interface{}) projector {
	t := schema["type"]
	switch t {
	case "object":
		return buildObjectProjector(schema["properties"].(map[string]interface{}))
	case "array":
		return buildArrayProject(schema["items"].(map[string]interface{}))
	default:
		return copyProjector
	}
}

func buildObjectProjector(properties map[string]interface{}) projector {
	projectors := map[string]projector{}
	for field, schema := range properties {
		projectors[field] = buildProjector(schema.(map[string]interface{}))
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

func buildArrayProject(items map[string]interface{}) projector {
	p := buildProjector(items)
	return func(v interface{}) interface{} {
		vm := v.([]interface{})
		obj := make([]interface{}, 0, len(vm))
		for _, val := range vm {
			obj = append(obj, p(val))
		}
		return obj
	}
}

type H = map[string]interface{}
type A = []interface{}

func TestProjection(t *testing.T) {

	p := buildProjector(H{
		"type": "object",
		"properties": H{
			"#id": H{
				"type": "number",
			},
			"#person": H{
				"type": "object",
				"properties": H{
					"age": H{
						"type": "number",
					},
				},
			},
			"#friends": H{
				"type": "array",
				"items": H{
					"type": "object",
					"properties": H{
						"age": H{
							"type": "number",
						},
						"name": H{
							"type": "string",
						},
					},
				},
			},
			"likes": H{
				"type": "array",
				"items": H{
					"type": "string",
				},
			},
		},
	})

	value := map[string]interface{}{
		"#id": 1,
		"#person": H{
			"age":  1,
			"age1": 1,
			"age2": 1,
		},
		"#friends": A{
			H{"age": 1, "name": "1", "other": 1},
			H{"age": 2, "name": "2", "other": "2"},
		},
		"likes": A{
			"a",
			"b",
		},
		"other": 1,
	}

	result := p(value)

	expected := map[string]interface{}{
		"#id": 1,
		"#person": H{
			"age": 1,
		},
		"#friends": A{
			H{"age": 1, "name": "1"},
			H{"age": 2, "name": "2"},
		},
		"likes": A{
			"a",
			"b",
		},
	}

	assert.Equal(t, expected, result)
}
