package main

import (
	"github.com/go-redis/redis/v8"
	"log"
)

func testNormal() {
	var testKey = "test"
	var setValue = "111111"

	client := NewRedisClientByOption()

	err := client.setKeyNoTTL(testKey, setValue)
	if err != nil {
		log.Printf("设置 key=%s 时发生错误，信息为：%v \n", testKey, err)
	} else {
		log.Printf("成功设置 key=%s 的值为 %s \n", testKey, setValue)
	}

	val, err := client.getKey(testKey)
	if err != nil {
		if err == redis.Nil {
			log.Printf("查询的 key=%s 为空 \n", testKey)
		} else {
			log.Printf("查询 key=%s 时发生错误，信息为：%v \n", testKey, err)
		}
	} else {
		log.Printf("查询 key=%s 的值为 %s \n", testKey, val)
	}

	result, err := client.getKeyByCustomerCommand(testKey)
	if err != nil {
		log.Printf("使用 DO 方法查询 key=%s 时发生错误，信息为：%v \n", testKey, err)
	} else {
		log.Printf("使用 DO 方法查询 key=%s 的值为 %s \n", testKey, result)
	}
}
