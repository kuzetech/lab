package _map

import (
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_factory(t *testing.T) {

	assertions := require.New(t)

	factoryMap := map[string]func(op int) int{}

	factoryMap["double"] = func(op int) int {
		return op * op
	}

	factoryMap["triple"] = func(op int) int {
		return op * op * op
	}

	assertions.Equal(8, factoryMap["triple"](2))

}
