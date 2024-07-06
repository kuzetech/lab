package _string

import (
	"github.com/stretchr/testify/require"
	"strconv"
	"testing"
)

func Test_strconv_util(t *testing.T) {
	assertions := require.New(t)

	s := strconv.Itoa(1)
	t.Log(s)

	i, err := strconv.Atoi("10")
	assertions.Nil(err)
	t.Log(i)

}
