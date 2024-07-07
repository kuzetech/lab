package pkg

import (
	"fmt"
	"testing"
)

/**
init 函数的特性：
	1. main 函数调用前，会根据包依赖的顺序，执行每一个包内的 init 函数
	2. 一个包里面可以有多个 init 函数
	3. 一个包内的同一个文件也可以有多个 init 函数，顺序执行
*/

func init() {
	fmt.Println(1)
}

func init() {
	fmt.Println(2)
}

func Test_init(t *testing.T) {
	fmt.Println(3)
}
