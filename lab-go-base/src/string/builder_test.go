package string

import (
	"bytes"
	"testing"
)

func Test_builder(t *testing.T) {
	var b bytes.Buffer
	b.WriteString("1")
	b.WriteString("2")

	// 12
	t.Log(string(b.Bytes()))
}
