package uuid

import (
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_UUIDV7(t *testing.T) {
	assertions := require.New(t)
	v7, err := uuid.NewV7()
	assertions.Nil(err)
	t.Logf("生成的 UUID v7 版本内容为：%s", v7)
}
