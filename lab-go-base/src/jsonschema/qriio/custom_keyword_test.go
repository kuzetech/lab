package qriio

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/qri-io/jsonpointer"
	"github.com/qri-io/jsonschema"
	"testing"
)

type IsFoo struct {
	Msg string `json:"test"`
}

// newIsFoo is a jsonschama.KeyMaker
func newIsFoo() jsonschema.Keyword {
	return new(IsFoo)
}

// Validate implements jsonschema.Keyword
func (f *IsFoo) Validate(propPath string, data interface{}, errs *[]jsonschema.KeyError) {}

// Register implements jsonschema.Keyword
func (f *IsFoo) Register(uri string, registry *jsonschema.SchemaRegistry) {}

// Resolve implements jsonschema.Keyword
func (f *IsFoo) Resolve(pointer jsonpointer.Pointer, uri string) *jsonschema.Schema {
	return nil
}

// ValidateKeyword implements jsonschema.Keyword
func (f *IsFoo) ValidateKeyword(ctx context.Context, currentState *jsonschema.ValidationState, data interface{}) {
	if str, ok := data.(string); ok {
		if str != "foo" {
			currentState.AddError(data, fmt.Sprintf("should be foo. plz make '%s' == foo. plz %s", str, f.Msg))
		}
	}
}

func Test_custom_keyword(t *testing.T) {
	// register a custom validator by supplying a function
	// that creates new instances of your Validator.
	jsonschema.RegisterKeyword("foo", newIsFoo)

	// If you register a custom validator, you'll need to manually register
	// any other JSON Schema validators you need.
	jsonschema.LoadDraft2019_09()

	schBytes := []byte(`{ 
		"foo": {
			"test": "test-content"
		} 
	}`)

	rs := &jsonschema.Schema{}
	if err := json.Unmarshal(schBytes, rs); err != nil {
		// Real programs handle errors.
		panic(err)
	}

	errs, err := rs.ValidateBytes(context.Background(), []byte(`"bar"`))
	if err != nil {
		panic(err)
	}

	fmt.Println(errs[0].Error())
}
