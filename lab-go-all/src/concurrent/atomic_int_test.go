package _concurrent

import (
	"github.com/stretchr/testify/assert"
	"sync/atomic"
	"testing"
)

/*
	重点：
		atomic 操作的是地址，千万不要传递值

*/

func Test_atomic(t *testing.T) {
	var num int64 = 0
	var numPtr *int64 = &num

	// 原值会改变，并且返回计算后的值
	newResult := atomic.AddInt64(numPtr, 1)
	assert.Equal(t, int64(1), num)
	assert.Equal(t, int64(1), newResult)

	// 如果需要比对旧值相等才替换，就使用 CompareAndSwap ，返回的结果标志是否替换成功
	isSuccess := atomic.CompareAndSwapInt64(numPtr, 1, 3)
	assert.Equal(t, true, isSuccess)
	assert.Equal(t, int64(3), num)

	// 如果只是想简单的替换可以使用 swap 方法就行，该方法还会返回旧值
	oldValue := atomic.SwapInt64(numPtr, 5)
	assert.Equal(t, int64(3), oldValue)
	assert.Equal(t, int64(5), num)

	// 单纯的存储值，并且该方法不会有任何的返回值
	atomic.StoreInt64(numPtr, 10)

	// 单纯的获取值
	loadInt64 := atomic.LoadInt64(numPtr)
	assert.Equal(t, int64(10), loadInt64)

}
