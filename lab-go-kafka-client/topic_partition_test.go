package main

import "testing"

type TopicPartitionMeta struct {
	Topic     string
	Partition int32
}

func TestTopicPartitionMetaMap(t *testing.T) {
	var m = make(map[TopicPartitionMeta]int)

	var t1 = TopicPartitionMeta{
		Topic:     "test",
		Partition: 1,
	}

	m[t1] = 1

	var t2 = TopicPartitionMeta{
		Topic:     "test",
		Partition: 1,
	}

	result, exist := m[t2]
	if !exist {
		t.Log("不存在")
	} else {
		t.Log(result)
	}

}
