package _httpclient

import (
	"bytes"
	"crypto/md5"
	"encoding/json"
	"fmt"
	"github.com/dgrijalva/jwt-go"
	"io/ioutil"
	"net/http"
	"sort"
	"strings"
	"testing"
)

func Test_yinli(t *testing.T) {

	// 请求的 URL
	url := "https://backend.gravity-engine.com/openapi/api/v1/report/user/list/"

	// 要发送的请求数据，假设是一个 JSON 格式的数据
	postData := map[string]interface{}{
		"app_id":          14608153,
		"user_filtering":  map[string]interface{}{},
		"event_filtering": map[string]interface{}{},
		"order_by_list": []map[string]interface{}{
			{
				"field": "CreateTime",
				"sort":  0,
			},
		},
		"page":      1,
		"page_size": 20,
	}

	sign := getSign(postData, "59789b5cb56a4d71873601b6f775e039")
	fmt.Println(sign)

	postData["sign"] = sign

	// 将请求数据编码为 JSON 格式
	jsonData, err := json.Marshal(postData)
	if err != nil {
		fmt.Println("JSON 序列化出错:", err)
		return
	}

	fmt.Println(string(jsonData))

	// 创建一个 HTTP POST 请求
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		fmt.Println("创建请求失败:", err)
		return
	}

	authorization := getAuthorization(sign, "59789b5cb56a4d71873601b6f775e039")
	fmt.Println(authorization)

	// 设置请求头，指定发送的是 JSON 格式的数据
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Authorization", authorization)

	// 创建一个 HTTP 客户端
	client := &http.Client{}

	// 发送请求并获取响应
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("请求失败:", err)
		return
	}
	defer resp.Body.Close()

	// 读取响应内容
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("读取响应失败:", err)
		return
	}

	// 打印响应状态码和内容
	fmt.Println("响应状态码:", resp.StatusCode)
	fmt.Println("响应内容:", string(body))

}

func getSign(params map[string]interface{}, appKey string) string {
	paramsList := make([]string, 0)
	for k, v := range params {
		if k == "sign" {
			continue
		}
		jsonString, _ := json.Marshal(v)
		paramString := fmt.Sprintf("%s=%s", k, jsonString)
		paramsList = append(paramsList, paramString)
	}
	sort.Strings(paramsList)
	currentString := strings.ReplaceAll(strings.ReplaceAll(strings.Join(paramsList, "&")+appKey, "\"", ""), " ", "")
	sign := fmt.Sprintf("%x", md5.Sum([]byte(currentString)))
	return sign
}

// 获取请求 authorization
// @param sign string 签名
// @param appKey 请求key
func getAuthorization(sign, appKey string) string {
	type MyClaims struct {
		AppKey string `json:"app_key"`
		jwt.StandardClaims
	}
	claims := MyClaims{
		AppKey:         appKey,
		StandardClaims: jwt.StandardClaims{},
	}
	t := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	token, _ := t.SignedString([]byte(sign))
	return token
}
