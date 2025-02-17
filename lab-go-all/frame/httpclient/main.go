package main

import (
	"fmt"
	"net/http"
	"net/url"
)

func main() {

	client := &http.Client{}

	baseURL := "http://localhost:8081/header"
	//baseURL := "http://localhost:8082/parameter/show"

	params := map[string]string{
		"user": "http://test",
		"age":  "30",
		"city": "beijing",
	}

	// 构建查询参数
	query := url.Values{}
	for key, value := range params {
		query.Add(key, value)
	}

	// 构建最终的 URL
	fullURL := baseURL + "?" + query.Encode()

	// 创建 HTTP 请求
	req, err := http.NewRequest(http.MethodGet, fullURL, nil)
	if err != nil {
		fmt.Println("Error creating request:", err)
		return
	}

	// 设置请求头
	req.Header.Set("User-Agent", "SpaHttpPing/1.0")
	req.Header.Del("Accept-Encoding")

	// 发送请求
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("Error sending request:", err)
		return
	}
	defer resp.Body.Close()

	fmt.Println("请求的 URL:", fullURL)
	fmt.Println("响应状态码:", resp.Status)
}
