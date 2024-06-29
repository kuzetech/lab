package flag

import (
	"fmt"
	"testing"
)
import flag "github.com/spf13/pflag"

func Test_pflag(t *testing.T) {

	// pflag是Go的flag包的直接替代，如果您在名称“ flag”下导入pflag (如：import flag "github.com/spf13/pflag")，则所有代码应继续运行且无需更改。
	// pflag 包与 flag 包的工作原理甚至是代码实现都是类似的，下面是 pflag 相对 flag 的一些优势：

	//  支持更加精细的参数类型：例如，flag 只支持 uint 和 uint64，而 pflag 额外支持 uint8、uint16、int32 等类型。
	// 支持更多参数类型：ip、ip mask、ip net、count、以及所有类型的 slice 类型。
	// 兼容标准 flag 库的 Flag 和 FlagSet：pflag 更像是对 flag 的扩展。
	// 原生支持更丰富的功能：支持 shorthand、deprecated、hidden 等高级功能。

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
