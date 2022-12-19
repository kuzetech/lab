package json

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

type H = map[string]interface{}

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
		},
	})

	value := map[string]interface{}{
		"a": 1,
		"b": H{
			"c": 1,
			"e": 5,
		},
		"d": 4,
	}

	result := p(value)

	expected := map[string]interface{}{
		"a": 1,
		"b": H{
			"c": 1,
		},
	}

	assert.Equal(t, expected, result)
}

func BenchmarkProjection(b *testing.B) {
	// parse schema, build projector
	p := buildSliceProjector(buildMapProjector([]string{"a", "b"}, copyProjector))

	value := []interface{}{
		map[string]interface{}{
			"a": 1,
			"b": 2,
			"c": 3,
		},
	}

	for i := 0; i < b.N; i++ {
		p(value)
	}
}
