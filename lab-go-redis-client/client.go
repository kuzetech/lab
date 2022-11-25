package main

import (
	"context"
	"github.com/go-redis/redis/v8"
	"time"
)

var (
	timeOutDuration = time.Second * 3
)

type RedisC struct {
	*redis.Client
}

func NewRedisClientByOption() *RedisC {

	rdb := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "", // no password set
		DB:       0,  // use default DB
	})
	return &RedisC{Client: rdb}
}

func NewRedisClientByParseURL() *RedisC {
	opt, err := redis.ParseURL("redis://<user>:<pass>@localhost:6379/<db>")
	if err != nil {
		panic(err)
	}

	rdb := redis.NewClient(opt)
	return &RedisC{Client: rdb}
}

func NewRedisClusterClient() *redis.ClusterClient {
	rdb := redis.NewClusterClient(&redis.ClusterOptions{
		Addrs: []string{"localhost:6379", "localhost:6380", "localhost:6381"},

		// To route commands by latency or randomly, enable one of the following.
		//RouteByLatency: true,
		//RouteRandomly: true,
	})

	return rdb
}

func (c *RedisC) getKey(key string) (string, error) {
	ctx, cancelFunc := context.WithTimeout(context.Background(), timeOutDuration)
	defer cancelFunc()

	result := c.Client.Get(ctx, key)
	err := result.Err()
	if err != nil {
		return "", err
	} else {
		return result.Val(), nil
	}
}

func (c *RedisC) setKeyNoTTL(key string, value interface{}) error {
	ctx, cancelFunc := context.WithTimeout(context.Background(), timeOutDuration)
	defer cancelFunc()

	result := c.Client.Set(ctx, key, value, -1)
	err := result.Err()
	return err
}

func (c *RedisC) setKeyAndTTL(key string, value interface{}, duration time.Duration) error {
	ctx, cancelFunc := context.WithTimeout(context.Background(), timeOutDuration)
	defer cancelFunc()

	result := c.Client.Set(ctx, key, value, duration)
	err := result.Err()
	return err
}

func (c *RedisC) getKeyByCustomerCommand(key string) (string, error) {
	ctx, cancelFunc := context.WithTimeout(context.Background(), timeOutDuration)
	defer cancelFunc()

	cmd := c.Client.Do(ctx, "get", key)
	if cmd.Err() != nil {
		return "", cmd.Err()
	} else {
		text, fmtErr := cmd.Text()
		if fmtErr != nil {
			return "", fmtErr
		} else {
			return text, nil
		}
	}
}
