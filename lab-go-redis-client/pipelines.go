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

	var result *redis.IntCmd

	// 一次性执行多条命令，但是不在一个事务内
	cmders, err := client.Client.Pipelined(ctx, func(pipe redis.Pipeliner) error {
		pipe.Set(ctx, testKey, "3", -1)
		result = pipe.Incr(ctx, testKey)
		return nil
	})

	if err != nil {
		log.Fatal(err)
	}

	log.Println(result.Val())

	log.Println(cmders[1].(*redis.IntCmd).Val())

	// 一次性执行多条命令，但是不在一个事务内
	// 该方法会比 Pipelined 更加方便
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
