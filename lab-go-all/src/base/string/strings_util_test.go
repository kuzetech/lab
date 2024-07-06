package _string

import (
	"github.com/stretchr/testify/require"
	"strings"
	"testing"
)

func Test_strings_util(t *testing.T) {
	assertions := require.New(t)

	s := []string{"a", "b", "c"}

	result := strings.Join(s, "-")

	assertions.Equal("a-b-c", result)

}
