package main

import (
	"fmt"
	"os"
)

// 通过 os.Args 获取传入参数，但是很难用
func main() {
	args := os.Args
	if args != nil {
		for _, arg := range args {
			fmt.Println(arg)
		}
	}
}
