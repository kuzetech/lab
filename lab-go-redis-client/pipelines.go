package main

import (
	"context"
	"github.com/go-redis/redis/v8"
	"log"
	"time"
)

func testPipelines() {
	var testKey = "pipeTest"

	client := NewRedisClientByOption()

	ctx, cancelFunc := context.WithTimeout(context.Background(), time.Second*3)
	defer cancelFunc()

	pipe := client.Client.Pipeline()

	pipe.Set(ctx, testKey, "1", -1)
	pipe.Incr(ctx, testKey)
	getResult := pipe.Get(ctx, testKey)

	resultArray, err := pipe.Exec(ctx)

	if err != nil {
		log.Fatal(err)
	}

	log.Println(getResult.Val())

	// log.Println(resultArray[0].(*redis.StringCmd).Val())
	log.Println(resultArray[2].(*redis.StringCmd).Val())

}
