package normal

import (
	"context"
	"github.com/go-redis/redis/v8"
	"github.com/stretchr/testify/require"
	"log"
	"testing"
)

func Test_pipeline_exec(t *testing.T) {
	assertions := require.New(t)

	client := NewDefaultRedisClient()

	pipe := client.c.Pipeline()

	pipe.Set(context.Background(), "hello", "world", -1)
	pipe.Get(context.Background(), "hello")
	pipe.TTL(context.Background(), "hello")

	resultArray, err := pipe.Exec(context.Background())
	assertions.Nil(err)

	log.Println(resultArray[0].(*redis.StringCmd).Val())
	log.Println(resultArray[1].(*redis.DurationCmd).Val())
	log.Println(resultArray[2].(*redis.DurationCmd).Val())
}
