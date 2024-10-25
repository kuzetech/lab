package _unsafe

import (
	"fmt"
	"reflect"
	"sync"
	"sync/atomic"
	"testing"
	"unsafe"
)

/*
	使用场景：
		1. 适用于调用外部的 C 程序，一般会有相关文档说明，本测试不包含
		2. 强制类型转换
		3. 原子性的加载指针
*/

type MyInt64 int64

func Test_force_transform(t *testing.T) {
	s := []int64{1, 2, 3}
	b := *(*[]MyInt64)(unsafe.Pointer(&s))

	for item := range b {
		itemType := reflect.TypeOf(item)
		t.Log(itemType.Kind()) // 全是 int，值得思考为什么不是 int64
	}
}

func Test_atomic(t *testing.T) {
	var shareBufPtr unsafe.Pointer

	writeDataFn := func() {
		data := make([]int, 0, 10)
		for i := 0; i < 10; i++ {
			data = append(data, i)
		}
		atomic.StorePointer(&shareBufPtr, unsafe.Pointer(&data))
	}

	readDataFn := func() {
		data := atomic.LoadPointer(&shareBufPtr)
		fmt.Println(data, *(*[]int)(data))
	}

	var wg sync.WaitGroup
	wg.Add(1)

	go func() {
		writeDataFn()
		wg.Done()
	}()

	wg.Wait()
	readDataFn()

}
