package file

import (
	"bufio"
	"github.com/stretchr/testify/assert"
	"io"
	"log"
	"os"
	"testing"
)

func init() {
	file, _ := os.Create("./files/read.txt")
	file.Write([]byte("\n"))
	file.Write([]byte("1111\n"))
	file.Write([]byte("2222\n"))
	file.Close()
}

func Test_read(t *testing.T) {
	file, _ := os.Open("./files/read.txt")
	r := bufio.NewReader(file)
	for {
		lineBytes, terr := r.ReadBytes('\n')
		// 第三次同时返回 3333 和 eof，因此需要选处理数据
		if len(lineBytes) > 0 {
			// 删除输出 '\n'
			// log.Println("读取到的内容为：" + string(lineBytes[:(len(lineBytes)-1)]))
			log.Println("读取到的内容为：" + string(lineBytes))
			if terr != nil {
				log.Println("错误为：" + terr.Error())
			}
		} else {
			log.Println("没有内容")
			if terr != nil {
				log.Println("错误为：" + terr.Error())
			}
		}
		if terr == io.EOF {
			break
		}
	}
}

func Test_readAll_not_exist(t *testing.T) {
	_, err := os.ReadFile("/a/b/not-exist.txt")
	assert.True(t, os.IsNotExist(err))
}
