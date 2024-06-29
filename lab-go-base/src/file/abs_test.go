package file

import (
	"path/filepath"
	"testing"
)

func Test_abs(t *testing.T) {
	path := "./files/noExistFile.log"

	// 即使文件不存在也能生成路径
	abs, _ := filepath.Abs(path)

	println(abs)
}
