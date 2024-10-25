package main

// pflag 是 Go 的 flag 包的直接替代，如果您在名称 “flag” 下导入 pflag (如：import flag "github.com/spf13/pflag")，则所有代码应继续运行且无需更改

import (
	"fmt"
	flag "github.com/spf13/pflag"
)

func main() {
	// 定义命令行参数对应的变量
	var cliName = flag.StringP("name", "n", "nick", "Input Your Name")
	var cliAge = flag.IntP("age", "a", 0, "Input Your Age")
	var cliGender = flag.StringP("gender", "g", "male", "Input Your Gender")
	var cliOK = flag.BoolP("ok", "o", false, "Input Are You OK")
	var cliDes = flag.StringP("des-detail", "d", "", "Input Description")

	// 以下两个不会在 --help 中显示
	// 但是当使用 -p 或者 --host 的时候，会输出提示消息

	// CommandLine 是默认的全局 FlagSet 对象
	// 弃用 port 的简写形式
	var cliPort = flag.IntP("port", "p", 0, "server port")
	flag.CommandLine.MarkShorthandDeprecated("port", "please use --port only")

	// 弃用标志
	var cliHost = flag.StringP("host", "h", "", "connect host")
	flag.CommandLine.MarkDeprecated("host", "please use --host instead")

	flag.Parse()

	fmt.Println("cliName", *cliName)
	fmt.Println("cliAge", *cliAge)
	fmt.Println("cliGender", *cliGender)
	fmt.Println("cliOK", *cliOK)
	fmt.Println("cliDes", *cliDes)
	fmt.Println("cliPort", *cliPort)
	fmt.Println("cliHost", *cliHost)
}
