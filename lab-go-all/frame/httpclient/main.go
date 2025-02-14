package main

import (
	"fmt"
	"net/http"
	"net/url"
)

func main() {
	baseURL := "http://localhost:8082/parameter/show"
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

	// 发送 GET 请求
	resp, err := http.Get(fullURL)
	if err != nil {
		fmt.Println("请求失败:", err)
		return
	}
	defer resp.Body.Close()

	fmt.Println("请求的 URL:", fullURL)
	fmt.Println("响应状态码:", resp.Status)
}
