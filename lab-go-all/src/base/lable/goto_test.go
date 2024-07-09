package _lable

import (
	"fmt"
	"testing"
)

func Test_goto(t *testing.T) {
	fmt.Println("1")
	goto End

	fmt.Println("2") // 该行不执行
End:
	fmt.Println(3)
}
