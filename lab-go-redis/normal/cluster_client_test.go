package normal

import (
	"context"
	"github.com/stretchr/testify/require"
	"log"
	"testing"
	"time"
)

func Test_cluster_client(t *testing.T) {
	assertions := require.New(t)

	client := NewClusterRedisClient()

	cmd := client.Set(context.Background(), "foo", "test", -1)
	assertions.Nil(cmd.Err())

	result := client.Get(context.Background(), "foo")
	assertions.Nil(result.Err())

	assertions.Equal("test", result.Val())
}

func Test_cluster_fail(t *testing.T) {
	client := NewClusterRedisClient()

	for i := 0; ; i++ {
		err := client.Set(context.Background(), "foo", i, 1*time.Minute).Err()
		if err != nil {
			log.Printf("get err: %v(%T)", err, err)
		} else {
			log.Println("set foo to", i)
		}
		time.Sleep(2 * time.Second)

		v, err := client.Get(context.Background(), "foo").Result()
		if err != nil {
			log.Printf("get err: %v(%T)", err, err)
		} else {
			log.Printf("foo=%v\n", v)
		}
	}
}
