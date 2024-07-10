package _concurrent

import (
	"go.uber.org/atomic"
	"testing"
)

/*
	还有一些比如关注度比较高的uber-go/atomic，它定义和封装了几种与常见类型相对应的原子操作类型，这些类型提供了原子操作的方法。
	这些类型包括 Bool、Duration、Error、Float64、Int32、Int64、String、Uint32、Uint64 等

	并且扩展了一些方法，
		比如 Bool 类型，提供了 CAS、Store、Swap、Toggle 等原子方法，还提供 String、MarshalJSON、UnmarshalJSON 等辅助方法
		比如 数组 类型，提供了 Sub 的减法等等
*/

func Test_atomic_uber(t *testing.T) {
	var atom atomic.Uint32
	atom.Store(42)
	atom.Sub(2)
	atom.CompareAndSwap(40, 11)
}
