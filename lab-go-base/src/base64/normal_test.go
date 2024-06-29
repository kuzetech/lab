package base64

import (
	"encoding/base64"
	"fmt"
	"testing"
)

/*
	* 作用
		base64编码是程序开发中常使用的编码格式，因为更适合不同的平台、不同的语言传输
		通常用于存储、传输一些二进制数据编码方法，即将二进制数据文本化（转化成ASCII）。
		比如有些系统只能使用ASCII字符，就可用base64将非ASCII字符数据转化为ASCII字符数据
	* 特点
		base64就是一种基于64个可以打印字符来表示二进制数据的方式，最终生成的长度不一定是 64 位
		如果是标准打印，A-Z(26)、a-z(26)、0-9(10)、+/(2):共计64个字符
		如果是URL打印，A-Z(26)、a-z(26)、0-9(10)、-_(2):共计64个字符
		编码后便于传输，尤其是不可见字符或特殊字符，对端接收后解码即可复原
		base64只是编码，并不具有加密作用
*/

func Test_normal(t *testing.T) {
	msg := "Hello, 世界"
	encoded := base64.StdEncoding.EncodeToString([]byte(msg))
	fmt.Println(encoded) // SGVsbG8sIOS4lueVjA==
	decoded, err := base64.StdEncoding.DecodeString(encoded)
	if err != nil {
		fmt.Println("decode error:", err)
		return
	}
	fmt.Println(string(decoded))
}

func Test_url(t *testing.T) {

	info := []byte("https://blog.csdn.net/qq_42412605?spm=1001.2100.3001.5343!？")

	standardResult := base64.StdEncoding.EncodeToString(info)

	fmt.Printf("标准编码：%s \n", standardResult)

	urlResult := base64.URLEncoding.EncodeToString(info)

	fmt.Printf("URL编码：%s \n", urlResult) //URL有一些特殊符号的处理

}
