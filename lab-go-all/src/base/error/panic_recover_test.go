package _error

import (
	"errors"
	"fmt"
	"testing"
)

func Test_panic_recover(t *testing.T) {
	defer func() {
		// recover 会捕获 panic 错误，并尝试恢复，程序是正常退出状态
		if err := recover(); err != nil {
			fmt.Println("recovered from ", err)
		}
	}()

	fmt.Println("start")

	panic(errors.New("something wrong"))
	// os.Exit(-1)  使用 exit 方法不会执行 defer，也不会打印错误调用栈信息，一般的程序不会使用这个方法，慎用
}
