package main

import (
	"context"
	"github.com/go-redis/redis/v8"
	"log"
	"time"
)

func testLuaScript() {

	// Underneath, Lua's number type is a float64 number that is used to store both ints and floats.
	// Because Lua does not distinguish ints and floats,
	// Redis always converts Lua numbers into ints discarding the decimal part,
	// for example, 3.14 becomes 3. If you want to return a float value,
	// return it as a string and parse the string in Go using Float64 helper.

	ctx, cancelFunc := context.WithTimeout(context.Background(), time.Second*3)
	defer cancelFunc()

	client := NewRedisClientByOption()

	var incrBy = redis.NewScript(`
		local key = KEYS[1]

		local sum = redis.call("GET", key)
		if not sum then
		  sum = 0
		end
		
		local num_arg = #ARGV
		for i = 1, num_arg do
		  sum = sum + ARGV[i]
		end
		
		redis.call("SET", key, sum)
		
		return sum
	`)

	keys := []string{"my_counter"}
	values := []interface{}{+1, +1, +1}
	num, _ := incrBy.Run(ctx, client.Client, keys, values...).Int()

	log.Println(num)

}
