package appconfig

import (
	"flag"
	"fmt"
	"testing"
)

var b = flag.Bool("b", false, "bool类型参数")
var s = flag.String("s", "", "string类型参数")

/*
	-flag 		只支持bool类型
	-flag x		只支持非bool类型
	-flag=x		都支持
*/

func Test_flag(t *testing.T) {
	// os.Args 除了包含传入的参数，还有其他的
	/*for idx, args := range os.Args {
		fmt.Println("参数"+strconv.Itoa(idx)+":", args)
	}*/

	flag.Parse()

	fmt.Println("-b:", *b)
	fmt.Println("-s:", *s)
	fmt.Println("其他参数：", flag.Args())
}
