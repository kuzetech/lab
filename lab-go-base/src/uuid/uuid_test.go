package uuid

import (
	"github.com/google/uuid"
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_uuid(t *testing.T) {
	assertions := require.New(t)
	newUUID, err := uuid.NewUUID()
	assertions.Nil(err)
	t.Log(newUUID.String())
}
