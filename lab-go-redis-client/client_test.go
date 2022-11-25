package main

import (
	"context"
	"log"
	"testing"
	"time"
)

func TestNewRedisClusterClient(t *testing.T) {
	client := NewRedisClusterClient()

	ctx, cancelFunc := context.WithTimeout(context.Background(), time.Second*3)
	defer cancelFunc()

	client.Set(ctx, "test", "2222", -1)

	result := client.Get(ctx, "test").Val()

	log.Println(result)
}
