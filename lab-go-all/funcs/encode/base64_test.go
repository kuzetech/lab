package encode

import (
	"encoding/base64"
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_base(t *testing.T) {
	assertions := require.New(t)

	msg := "Hello"
	encodeResult := base64.StdEncoding.EncodeToString([]byte(msg))
	assertions.Equal("SGVsbG8=", encodeResult)

	decodedResult, err := base64.StdEncoding.DecodeString(encodeResult)
	assertions.Nil(err)
	assertions.Equal(msg, string(decodedResult))
}

// URL 传递参数时包含非法字符
// https://blog.csdn.net/qq_42412605?spm=555!?/666
func Test_url(t *testing.T) {
	info := []byte("555!?/666")
	result := base64.URLEncoding.EncodeToString(info)
	t.Logf("URL编码：%s \n", result)
}
