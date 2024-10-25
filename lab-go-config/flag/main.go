package main

import (
	"flag"
	"fmt"
)

/*
	-flag 		只支持bool类型
	-flag x		只支持非bool类型
	-flag=x		都支持
*/

func main() {
	wordPtr := flag.String("word", "foo", "a string")
	numbPtr := flag.Int("numb", 42, "an int")
	boolPtr := flag.Bool("fork", false, "a bool")

	// 未调用解析前如果取值将得到默认值
	fmt.Println("word:", *wordPtr)
	fmt.Println("numb:", *numbPtr) // 对指针取值
	fmt.Println("fork:", *boolPtr)

	var svar string
	flag.StringVar(&svar, "svar", "bar", "a string var") // 对变量取址

	// 解析输入
	flag.Parse()

	fmt.Println("word:", *wordPtr)
	fmt.Println("numb:", *numbPtr) // 对指针取值
	fmt.Println("fork:", *boolPtr)
	fmt.Println("svar:", svar)
	fmt.Println("tail:", flag.Args())
}
