package _test

import (
	"strings"
	"testing"
)

func joinStringByArray(arr []string) string {
	ret := ""
	for _, s := range arr {
		ret = ret + s
	}
	return ret
}

func joinStringByBuilder(arr []string) string {
	var builder strings.Builder
	for _, s := range arr {
		builder.WriteString(s)
	}
	return builder.String()
}

func Benchmark_joinStringByArray(b *testing.B) {
	arr := []string{"a", "b", "c", "d", "e"}
	b.StartTimer()
	for i := 0; i < b.N; i++ {
		joinStringByArray(arr)
	}
	b.StopTimer()
}

func Benchmark_joinStringByBuilder(b *testing.B) {
	arr := []string{"a", "b", "c", "d", "e"}
	b.StartTimer()
	for i := 0; i < b.N; i++ {
		joinStringByBuilder(arr)
	}
	b.StopTimer()
}

/*
	压测过程默认会控制在 1s 以内结束，由于程序也不知道具体要执行多少次，所以 N 会逐步增加，直到差不多 1s
	通过添加参数 -test.benchtime=2s 可以控制压测时间
	通过添加参数 -test.benchtime=30x 可以控制压测次数
	通过添加参数 -test.count=2 可以控制压测轮数

	大致会输出如下内容：

	Benchmark_joinStringByArray
	Benchmark_joinStringByArray-12      	10618539	       103.2 ns/op
	Benchmark_joinStringByBuilder
	Benchmark_joinStringByBuilder-12    	34935163	        34.62 ns/op

	其中 Benchmark_joinStringByBuilder-12 中 12 指的是测试的 cpu 核数
	通过添加程序参数 -test.cpu=5 可以控制压测核数
	通过添加程序参数 -test.cpu=1,2,3 可以控制指定测试的核编号

	通过添加参数 -test.benchmem 可以输出内存分配的次数，更容易看出性能差异，输出的内容如下：

	Benchmark_joinStringByArray
	Benchmark_joinStringByArray-12      	10618539	       103.2 ns/op	      16 B/op	       4 allocs/op
	Benchmark_joinStringByBuilder
	Benchmark_joinStringByBuilder-12    	34935163	        34.62 ns/op	       8 B/op	       1 allocs/op



*/
