package normal

import (
	"context"
	"github.com/stretchr/testify/require"
	"math/rand"
	"testing"
)

func Test_cluster_pipeline(t *testing.T) {
	assertions := require.New(t)

	client := NewClusterRedisClient()

	pipeline := client.Pipeline()
	pipeline.Set(context.Background(), "1", rand.Int(), -1)
	pipeline.Publish(context.Background(), "555", "test")

	cmds, err := pipeline.Exec(context.Background())
	assertions.Nil(err)

	for _, cmd := range cmds {
		assertions.Nil(cmd.Err())
	}
}
