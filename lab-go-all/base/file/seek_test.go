package file

import (
	"bufio"
	"fmt"
	"io"
	"log"
	"os"
	"strings"
	"testing"
)

func Test_seek(t *testing.T) {
	var filePath = "./files/seek.txt"

	file, _ := os.Create(filePath)
	defer file.Close()

	var byte1 = []byte("1111\n")
	file.Write(byte1)

	var byte2 = []byte("2222\n")
	file.Write(byte2)

	var byte3 = []byte("3333\n")
	file.Write(byte3)

	//state, _ := os.Stat(filePath)
	//var fileSize = state.Size()

	// 0 表示从文件头部开始
	// 4 表示移动到第四个字节
	_, err := file.Seek(4, 0)
	if err != nil {
		log.Fatal(err)
	}

	r := bufio.NewReader(file)

	for {
		lineBytes, terr := r.ReadBytes('\n')
		line := strings.TrimSpace(string(lineBytes))
		if terr == io.EOF {
			break
		}
		if len(line) > 0 {
			fmt.Println(line)
		}

	}
}
