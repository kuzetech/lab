package file

import (
	"fmt"
	"os"
	"syscall"
	"testing"
)

func Test_get_create_time(t *testing.T) {
	var filePath = "./files/createTime.txt"

	file, _ := os.Create(filePath)
	file.Write([]byte("1111\n"))
	file.Close()

	state, _ := os.Stat(filePath)
	fileAttr := state.Sys().(*syscall.Stat_t)

	// Atimespec Ctimespec Mtimespec 适用于 darwin 系统
	// Atim Ctim Mtim 适用于 linux 系统
	// 同一段代码文件以 _linux _darwin 等标识结尾，go 会根据编译变量的不同使用不同的实现文件
	fmt.Println("上一次访问时间", timeSpecToTime(fileAttr.Atimespec))
	fmt.Println("创建时间   ", timeSpecToTime(fileAttr.Ctimespec))
	fmt.Println("最后修改时间", timeSpecToTime(fileAttr.Mtimespec))
}
