package normal

import (
	"context"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/require"
	"testing"
)

var key1 = "key1"
var key2 = "key2"

func Test_watch_success(t *testing.T) {
	assertions := require.New(t)

	client := NewDefaultRedisClient()

	err := client.setKeyNoTTL(key1, 1)
	assertions.Nil(err)

	err = client.setKeyNoTTL(key2, 1)
	assertions.Nil(err)

	// 监听 keys 的状态
	// 仅当 key 未被其他客户端修改才会执行命令
	err = client.c.Watch(
		context.Background(),
		func(tx *redis.Tx) error {
			cmd := tx.Incr(context.Background(), key2)
			return cmd.Err()
		},
		key1,
	)
	assertions.Nil(err)

	err = client.setKeyNoTTL(key1, 2)
	assertions.Nil(err)

	result, exist, err := client.getIntValueByKey(key2)
	assertions.Nil(err)
	assertions.True(exist)
	assertions.Equal(2, result)
}

func Test_watch_fair(t *testing.T) {
	assertions := require.New(t)

	client := NewDefaultRedisClient()

	err := client.setKeyNoTTL(key1, 1)
	assertions.Nil(err)

	err = client.setKeyNoTTL(key2, 1)
	assertions.Nil(err)

	// 监听 keys 的状态
	// 仅当 key 未被其他客户端修改才会执行命令
	err = client.c.Watch(
		context.Background(),
		func(tx *redis.Tx) error {
			cmd := tx.Incr(context.Background(), key2)
			return cmd.Err()
		},
		key1,
	)
	assertions.Nil(err)

	err = client.setKeyNoTTL(key1, 2)
	assertions.Nil(err)

	err = client.setKeyNoTTL(key2, 3)
	assertions.Nil(err)

	result, exist, err := client.getIntValueByKey(key2)
	assertions.Nil(err)
	assertions.True(exist)
	assertions.Equal(3, result)
}
