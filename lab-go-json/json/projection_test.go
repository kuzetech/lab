package json

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

type H = map[string]interface{}
type A = []interface{}

func TestProjection(t *testing.T) {

	p := buildProjector(H{
		"type": "object",
		"properties": H{
			"a": H{
				"type": "number",
			},
			"b": H{
				"type": "object",
				"properties": H{
					"c": H{
						"type": "number",
					},
				},
			},
			"d": H{
				"type": "array",
				"items": H{
					"type": "object",
					"properties": H{
						"e": H{
							"type": "number",
						},
						"f": H{
							"type": "string",
						},
					},
				},
			},
			"f": H{
				"type": "array",
				"items": H{
					"type": "string",
				},
			},
		},
	})

	value := map[string]interface{}{
		"a": 1,
		"b": H{
			"c":  1,
			"c1": 1,
			"c2": 1,
		},
		"d": A{
			H{"e": 1, "f": "1", "e1": 1},
			H{"e": 2, "f": "2", "f1": "2"},
		},
		"f": A{
			"a",
			"b",
		},
		"z": 1,
	}

	result := p(value)

	expected := map[string]interface{}{
		"a": 1,
		"b": H{
			"c": 1,
		},
		"d": A{
			H{"e": 1, "f": "1"},
			H{"e": 2, "f": "2"},
		},
		"f": A{
			"a",
			"b",
		},
	}

	assert.Equal(t, expected, result)
}
