package file

import (
	"bytes"
	jsoniter "github.com/json-iterator/go"
	"github.com/stretchr/testify/require"
	"io"
	"io/ioutil"
	"log"
	"testing"
)

func Test_LimitReader(t *testing.T) {
	assertions := require.New(t)

	var bodyBytes []byte = []byte(`{"first":"Leonid"}`)

	buffer := bytes.NewBuffer(bodyBytes)

	limitReader := io.LimitReader(buffer, 5)

	decoder := jsoniter.ConfigFastest.NewDecoder(limitReader)

	var v interface{}
	if err := decoder.Decode(&v); err != nil {
		// 如果达到了读取上限，会先返回数据，但因为数据不完整所以，json 序列化报错
		// unexpected end of input, error found in #5 byte of ...|{"fir|..., bigger context ...|{"fir|..
		log.Println(err)
		limitReader := limitReader.(*io.LimitedReader)
		if limitReader.N <= 0 {
			log.Println("n < 0")
		}
		if err == io.EOF {
			log.Println("eof")
		}

		// 当我接着读时，只能获取到剩余的内容
		// st":"Leonid"}
		all, err := ioutil.ReadAll(buffer)
		assertions.Nil(err)
		log.Println(string(all))

	}

}

func Test_reader(t *testing.T) {
	assertions := require.New(t)

	var bodyBytes []byte = []byte("test")

	buffer := bytes.NewBuffer(bodyBytes)

	readCloser := ioutil.NopCloser(buffer)

	bodyBytes, err := ioutil.ReadAll(readCloser)
	assertions.Nil(err)

	log.Println(bodyBytes)

}

func Test_read_all_limit(t *testing.T) {
	assertions := require.New(t)

	var bodyBytes []byte = []byte(`{"t":"d"}`)

	buffer := bytes.NewBuffer(bodyBytes)

	// 比最长的限制多一位
	reader := io.LimitReader(buffer, int64(len(bodyBytes)+1))

	// 读取出所有数据
	all, err := ioutil.ReadAll(reader)
	assertions.Nil(err)
	limitReader := reader.(*io.LimitedReader)
	log.Println(string(all))
	log.Println(limitReader.N)

	// 全部读出后继续读取，也不会报错，因为底层是 buffer
	all2, err := ioutil.ReadAll(reader)
	log.Println(err)
	log.Println(string(all2))

	log.Println(len(all))
}
