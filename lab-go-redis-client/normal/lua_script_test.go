package normal

import (
	"context"
	"github.com/go-redis/redis/v8"
	"github.com/stretchr/testify/require"
	"log"
	"testing"
)

/*
	使用 lua script 时需要特别注意：

	Underneath, Lua's number type is a float64 number that is used to store both ints and floats.
	Because Lua does not distinguish ints and floats,
	Redis always converts Lua numbers into ints discarding the decimal part,
	for example, 3.14 becomes 3. If you want to return a float value,
	return it as a string and parse the string in Go using Float64 helper.
*/

func Test_lua_script(t *testing.T) {
	should := require.New(t)

	client := NewDefaultRedisClient()

	var incrBy = redis.NewScript(`
		local key = KEYS[1]
		
		-- call 方法会直接抛出异常，停止程序
		-- 可以使用 pcall 方法捕获并处理异常
		local sum = redis.call("GET", key)  --  
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

	cmd := incrBy.Run(context.Background(), client.c, keys, values...)
	should.Nil(cmd.Err())

	result, err := cmd.Int()
	should.Nil(err)

	log.Println(result)
}
