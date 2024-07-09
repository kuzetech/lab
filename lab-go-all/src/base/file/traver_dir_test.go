package file

import (
	"fmt"
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"os"
	"path/filepath"
	"testing"
)

func Test_traver_dir(t *testing.T) {
	files, _ := ioutil.ReadDir("/Users/huangsw/code/study/study-golang/file/files")
	for _, file := range files {
		if file.IsDir() {
			println("文件夹：" + file.Name())
		} else {
			println("文件：" + file.Name())
		}
	}
}

func Test_traver_dir2(t *testing.T) {
	var files []string

	err := filepath.Walk("./files", func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			absPath, err := filepath.Abs(path)
			if err != nil {
				return err
			}
			files = append(files, absPath)
		}
		return nil
	})

	assertions := require.New(t)
	assertions.Nil(err)

	for _, file := range files {
		fmt.Println(file)
	}
}

func Test_parent_dir(t *testing.T) {
	dir := filepath.Dir("/a/b")
	t.Log(dir)
}
