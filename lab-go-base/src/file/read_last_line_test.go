package file

import (
	"bufio"
	"fmt"
	"github.com/stretchr/testify/require"
	"os"
	"testing"
)

func getOffset(path string) (uint64, error) {
	file, err := os.Open(path)
	if err != nil {
		return 0, err
	}
	defer file.Close()

	fileInfo, err := file.Stat()
	if err != nil {
		return 0, err
	}

	if fileInfo.Size() == 0 {
		return 0, nil
	}

	for i := int64(1); i <= fileInfo.Size(); i++ {
		offset, err := file.Seek(-1*i, 2)
		if err != nil {
			return 0, err
		}

		b := make([]byte, 1)
		_, err = file.Read(b)
		if err != nil {
			return 0, err
		}

		if string(b) == "\n" {
			return uint64(offset + 1), nil
		}
	}

	return 0, nil
}

func Test_read_last_line(t *testing.T) {
	assertions := require.New(t)

	result, err := getOffset("/Users/kuze/code/study/study-golang/file/1.log")
	assertions.Nil(err)

	fmt.Println(result)

}

func Test_seek_log3(t *testing.T) {
	assertions := require.New(t)

	file, err := os.Open("./files/2023-08-04.3.log")
	assertions.Nil(err)

	defer file.Close()

	_, err = file.Seek(6312, 0)
	assertions.Nil(err)

	scanner := bufio.NewScanner(file)
	scanner.Scan()
	line := scanner.Text()

	fmt.Println(line)
}

func Test_seek_log2(t *testing.T) {
	assertions := require.New(t)

	file, err := os.Open("./files/2023-08-04.2.log")
	assertions.Nil(err)

	defer file.Close()

	_, err = file.Seek(11286643, 0)
	assertions.Nil(err)

	scanner := bufio.NewScanner(file)
	scanner.Scan()
	line := scanner.Text()

	fmt.Println(line)
}
