package normal

import (
	"context"
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_client(t *testing.T) {
	assertions := require.New(t)
	client := NewDefaultRedisClient()

	// 1   MOVED 172.18.0.12:6379
	// 555 MOVED 172.18.0.14:6379
	err := client.setKeyNoTTL("555", "1")

	assertions.Nil(err)
}

func Test_pub_sub_move(t *testing.T) {
	assertions := require.New(t)
	client := NewDefaultRedisClient()

	// 1   MOVED 172.18.0.12:6379
	// 555 MOVED 172.18.0.14:6379
	err := client.c.Publish(context.Background(), "555", "1").Err()
	assertions.Nil(err)

}
