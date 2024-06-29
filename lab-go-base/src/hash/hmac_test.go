package hash

import (
	"crypto/hmac"
	"crypto/md5"
	"crypto/sha256"
	"encoding/base64"
	"fmt"
	"testing"
)

func Test_hmac(t *testing.T) {

	h := hmac.New(sha256.New, []byte("demo"))

	h.Write([]byte("POST"))
	h.Write([]byte("/v1/collect"))
	h.Write([]byte("demo"))
	h.Write([]byte("123"))
	h.Write([]byte("123"))
	// 写入 nil 和 不调用的结果一样
	h.Write([]byte("{\"batchId\":\"123\",\"messages\":[{\"type\":\"event\",\"data\":{\"a\":3,\"b\":\"3\"}}]}"))

	fmt.Println(base64.StdEncoding.EncodeToString(h.Sum(nil)))
}

func Test_md5(t *testing.T) {

	// 待加密字符串
	s := "https://space.bilibili.com/480883651"

	// 进行md5加密，因为Sum函数接受的是字节数组，因此需要注意类型转换
	srcCode := md5.Sum([]byte(s))

	// md5.Sum函数加密后返回的是字节数组，需要转换成16进制形式
	code := fmt.Sprintf("%x", srcCode)

	fmt.Println(string(code))
}
