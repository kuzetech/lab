package normal

import (
	"context"
	"errors"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
)

var (
	defaultTimeOutDuration    = time.Millisecond * 150
	defaultRedisLocalhostAddr = "172.18.0.14:6379"
	defaultRedisDatabase      = 0
	defaultRedisUser          = ""
	defaultRedisPassword      = ""
)

type RedisClient struct {
	c *redis.Client
}

func NewDefaultRedisClient() *RedisClient {
	rdb := redis.NewClient(&redis.Options{
		Addr:     defaultRedisLocalhostAddr,
		Password: defaultRedisPassword,
		DB:       defaultRedisDatabase,
	})
	return &RedisClient{c: rdb}
}

func NewClusterRedisClient() redis.UniversalClient {
	rdb := redis.NewUniversalClient(&redis.UniversalOptions{
		Addrs: []string{
			"172.18.0.11:6379",
			"172.18.0.12:6379",
			"172.18.0.13:6379",
			"172.18.0.14:6379",
			"172.18.0.15:6379",
			"172.18.0.16:6379",
		},
		Password: defaultRedisPassword,
		DB:       defaultRedisDatabase,
	})
	return rdb
}

func (c *RedisClient) getStringValueByKey(key string) (string, bool, error) {
	result := c.c.Get(context.Background(), key)
	err := result.Err()
	if err == nil {
		return result.Val(), true, nil
	}
	// key 不存在
	if errors.Is(err, redis.Nil) {
		return "", false, nil
	}
	return "", false, err
}

func (c *RedisClient) getIntValueByKey(key string) (int, bool, error) {
	result := c.c.Get(context.Background(), key)
	err := result.Err()
	if err == nil {
		v, err := result.Int()
		if err != nil {
			return 0, false, err
		}
		return v, true, nil
	}
	// key 不存在
	if errors.Is(err, redis.Nil) {
		return 0, false, nil
	}
	return 0, false, err
}

func (c *RedisClient) setKeyNoTTL(key string, value interface{}) error {
	result := c.c.Set(context.Background(), key, value, -1)
	err := result.Err()
	return err
}

func (c *RedisClient) setKeyAndTTL(key string, value interface{}, duration time.Duration) error {
	result := c.c.Set(context.Background(), key, value, duration)
	err := result.Err()
	return err
}

func (c *RedisClient) execCommand(command string, args ...interface{}) (string, error) {
	cmd := c.c.Do(context.Background(), command, args)
	if cmd.Err() != nil {
		return "", cmd.Err()
	}
	return cmd.String(), nil
}

func (c *RedisClient) scanKeysByPrefix(prefix string) {
	// 可以使用 c.Client.Keys() 实现
	// 但是 c.Client.Scan 性能更好
	iter := c.c.Scan(context.Background(), 0, prefix, 0).Iterator()
	for iter.Next(context.Background()) {
		fmt.Println("keys", iter.Val())
	}
	if err := iter.Err(); err != nil {
		panic(err)
	}
}
