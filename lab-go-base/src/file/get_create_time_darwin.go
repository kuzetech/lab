package file

import (
	"fmt"
	"os"
	"syscall"
	"time"
)

func timeSpecToTime(ts syscall.Timespec) string {
	return time.Unix(ts.Sec, ts.Nsec).Format("2006-01-02 15:04:05")
}

func getCreateTimeByDarwin() {
	var filePath = "./files/createTime.txt"

	file, _ := os.Create(filePath)
	file.Write([]byte("1111\n"))
	file.Close()

	state, _ := os.Stat(filePath)
	fileAttr := state.Sys().(*syscall.Stat_t)

	fmt.Println("上一次访问时间", timeSpecToTime(fileAttr.Atimespec))
	fmt.Println("创建时间   ", timeSpecToTime(fileAttr.Ctimespec))
	fmt.Println("最后修改时间", timeSpecToTime(fileAttr.Mtimespec))
}
