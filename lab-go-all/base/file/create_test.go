package file

import (
	"os"
	"testing"
)

func Test_create(t *testing.T) {
	var filePath = "./files/create"

	// 文件不存在则创建
	// 文件存在则清空内容
	file, _ := os.Create(filePath)
	defer file.Close()
}
