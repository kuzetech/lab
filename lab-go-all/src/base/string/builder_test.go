package _string

import (
	"strings"
	"testing"
)

func Test_builder(t *testing.T) {
	var builder strings.Builder
	builder.WriteString("1")
	builder.WriteString("2")
	builder.WriteString("3")
	t.Log(builder.String())
}
