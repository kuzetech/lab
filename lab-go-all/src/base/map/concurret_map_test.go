package _map

import (
	cmap "github.com/orcaman/concurrent-map/v2"
	"github.com/stretchr/testify/assert"
	"sync"
	"testing"
)

/*
	go 现阶段提供了三种思路：
		1. Lock + map 自实现，比较麻烦还得自己实现，一般不用
		2. sync.Map，官方实现
			适用场景：一个 key 写入后基本不怎么修改，基本都是读
			核心原理：
				内部使用两个map，read和dirty。
				read是挡在读写操作的第一个屏障，如果读写的key在这个read中直接在read中读写，这里还是得使用锁保证并发。
				而dirty的作用就相当于是一个缓冲区，一旦要写的key在read中找不到，我们就会先写dirty中。这个好处是不影响读read的操作，不会出现并发读写一个数据结构的情况。
				然后当 dirty 到达一定数量的时候合并 dirty 到 read 中
		3. concurrent-map，比较成熟的第三方库
			适用场景：除了 sync.Map 场景
			核心原理：
				使用分片锁，将哈希表分成多个小的哈希表片段，并为每个片段分配一个独立的锁。
*/

func Test_sync_map(t *testing.T) {
	var m sync.Map
	m.Store("test", "test")
	value, ok := m.Load("test")
	assert.Equal(t, true, ok)
	assert.Equal(t, "test", value.(string))
}

func Test_concurrent_map(t *testing.T) {
	m := cmap.New[string]()

	// Sets item within map, sets "bar" under key "foo"
	m.Set("foo", "bar")

	// Retrieve item from map.
	bar, ok := m.Get("foo")
	assert.Equal(t, true, ok)
	assert.Equal(t, "bar", bar)

	// Removes item under key "foo"
	m.Remove("foo")
}
