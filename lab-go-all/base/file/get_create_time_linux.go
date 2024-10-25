package file

import (
	"fmt"
	"os"
	"syscall"
)

func getCreateTimeByLinux() {
	var filePath = "./files/createTime.txt"

	file, _ := os.Create(filePath)
	file.Write([]byte("1111\n"))
	file.Close()

	state, _ := os.Stat(filePath)
	fileAttr := state.Sys().(*syscall.Stat_t)

	fmt.Println("上一次访问时间", timeSpecToTime(fileAttr.Atim))
	fmt.Println("创建时间   ", timeSpecToTime(fileAttr.Ctim))
	fmt.Println("最后修改时间", timeSpecToTime(fileAttr.Mtim))
}
