package main

import (
	"context"
	"github.com/go-redis/redis/v8"
	"log"
	"testing"
	"time"
)

func Test_testNormal(t *testing.T) {
	client := NewRedisClientByOption()

	key := "batch-state/" + "test" + "/" + "hsw2"

	for {
		pipe := client.Client.Pipeline()

		pipe.Get(context.Background(), key)
		pipe.TTL(context.Background(), key)

		resultArray, err := pipe.Exec(context.Background())

		if err != nil {
			if err.Error() != "redis: nil" {
				log.Fatal(err)
			} else {
				log.Println("值还不存在")
				time.Sleep(time.Millisecond * 300)
				continue
			}
		}

		log.Println(resultArray[0].(*redis.StringCmd).Val())
		log.Println(resultArray[1].(*redis.DurationCmd).Val())

		time.Sleep(time.Millisecond * 300)
	}

}
